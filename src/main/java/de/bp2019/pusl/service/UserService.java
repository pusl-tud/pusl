package de.bp2019.pusl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.Query;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.views.LoginView;
import de.bp2019.pusl.util.LimitOffsetPageRequest;
import de.bp2019.pusl.util.Utils;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * Service providing relevant Users. Implements {@link UserDetailsService} to be
 * used with Spring Security
 * 
 * @author Leon Chemnitz
 */
@Service
public class UserService extends AbstractDataProvider<User, String> {
    private static final long serialVersionUID = 1866448855648692985L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    /**
     * Returns the currently logged in User as a {@link User} Object. If Current
     * user isn't found in DB, user is logged out
     * 
     * @return current User
     * @author Leon Chemnitz
     * @throws DataNotFoundException
     */
    public User currentUser() {
        LOGGER.debug("getting current user");

        LOGGER.debug("fetching currentUser from database");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> user = userRepository.findByEmailAddress(email);

        if (user.isEmpty()) {
            LOGGER.info("currently logged in user with email: '" + email
                    + "' was not found in Database. User is being logged out.");
            SecurityContextHolder.clearContext();
            UI.getCurrent().getSession().close();
            UI.getCurrent().navigate(LoginView.ROUTE);
            ErrorDialog.open("Angemeldeter Nutzer existiert nicht mehr in Datenbank!");
            return new User();
        }

        return user.get();
    }

    /**
     * Returns the Full name of the currently logged in User. Returns the users
     * email address if no firstname is set
     * 
     * @return Full name of current user
     * @author Leon Chemnitz
     */
    public String currentUserFullName() {
        return getFullName(currentUser());
    }

    /**
     * @return Type of current User
     * @author Leon Chemnitz
     */
    public UserType currentUserType() {
        return currentUser().getType();
    }

    /**
     * @return Institutes associated with current user
     * @author Leon Chemnitz
     */
    public Set<Institute> currentUserInstitutes() {
        return currentUser().getInstitutes();
    }

    /**
     * Get the full name of a user. Returns the users email address if no firstname
     * is set
     * 
     * @param user User
     * @return Full name of the found User. Empty String if no user is found
     * @author Leon Chemnitz
     */
    public static String getFullName(User user) {
        if (user != null) {
            LOGGER.info(user.toString());
            /* initial admin has no name */
            if (user.getFirstName() == null || user.getFirstName().equals("")) {
                return user.getEmailAddress();
            } else {
                return user.getFirstName() + " " + user.getLastName();
            }
        } else {
            return "";
        }
    }

    /**
     * Save one User
     *
     * @param user to persist
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void save(User user) throws UnauthorizedException {
        LOGGER.info("saving user");
        LOGGER.debug(user.toString());

        if (userIsAuthorized(user)) {
            userRepository.save(user);
        } else {
            LOGGER.error("user is not authorized to access User!");
            throw new UnauthorizedException();
        }
    }

    /**
     * Delete a User
     *
     * @param user to delete
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void delete(User user) throws UnauthorizedException {
        LOGGER.info("deleting user");
        LOGGER.debug(user.toString());

        if (userIsAuthorized(user)) {
            userRepository.delete(user);
        } else {
            LOGGER.error("user is not authorized to access User!");
            throw new UnauthorizedException();
        }
    }

    /**
     * Get a User based on its Id. Only returns Users the active User is
     * authenticated to see.
     * 
     * @param id Id to search for
     * @return found User with matching Id, null if none is found
     * @author Leon Chemnitz
     * @throws DataNotFoundException
     * @throws UnauthorizedException
     */
    public User getById(String id) throws DataNotFoundException, UnauthorizedException {
        LOGGER.info("checking if user with id " + id + " is present");
        Optional<User> foundUser = userRepository.findById(id);

        if (foundUser.isEmpty()) {
            LOGGER.info("not found in database");
            throw new DataNotFoundException();
        } else {

            LOGGER.info("found in database");
            User user = foundUser.get();
            LOGGER.debug(user.toString());

            if (userIsAuthorized(user)) {
                LOGGER.info("returned because user is authorized");
                return user;
            } else {
                LOGGER.error("user is not authorized to access User!");
                throw new UnauthorizedException();
            }
        }
    }

    /**
     * Check if current user is authorized to access the {@link User}
     * 
     * @param user
     * @return
     * @author Leon Chemnitz
     */
    private boolean userIsAuthorized(User user) {

        switch (currentUserType()) {
            default:
            case HIWI:
            case WIMI:
                break;
            case ADMIN:
                if (!Utils.containsAny(currentUserInstitutes(), user.getInstitutes()))
                    break;
            case SUPERADMIN:
                return true;
        }
        return false;
    }

    /**
     * Check wether a {@link User} with a given email address already exists in
     * Database. Also takes an id parameter which excludes the entity with matching
     * Id from the check. This is neccessairy for updating an existing {@link User}.
     * 
     * @param name
     * @param id
     * @return
     * @author Leon Chemnitz
     */
    public boolean checkEmailAvailable(String email, Optional<ObjectId> id) {
        LOGGER.info("checking if user email address '" + email + "' exists in database");

        Optional<User> foundUser = userRepository.findByEmailAddress(email);

        if (!foundUser.isPresent()) {
            LOGGER.info("email is available because no user with email address '" + email + "' was found");
            return true;
        }

        ObjectId foundId = foundUser.get().getId();

        if (id.isPresent() && foundId.equals(id.get())) {
            LOGGER.info("email is available because it's the email of the current User");
            return true;
        } else {
            LOGGER.info("email is already taken by User with id " + foundId.toString());
            return false;
        }
    }

    /**
     * Returns all the UserTypes the current User is allowed to act upon.
     * 
     * @return
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public List<UserType> getUserTypes() {
        switch (currentUserType()) {
            case SUPERADMIN:
                return Arrays.asList(UserType.values());
            case ADMIN:
                return Arrays.asList(UserType.ADMIN, UserType.WIMI, UserType.HIWI);
            default:
                return new ArrayList<UserType>();
        }
    }

    /**
     * @author Leon Chemnitz
     */
    @Override
    public boolean isInMemory() {
        return false;
    }

    /**
     * 
     * @param query
     * @param filter
     * @return
     * @author Leon Chemnitz
     */
    public int sizeHiwis(Query<User, String> query, Set<Institute> institutes) {
        switch (currentUserType()) {
            case SUPERADMIN:
                return (int) userRepository.countByInstitutesInAndType(institutes, UserType.HIWI);
            case ADMIN:
                Set<Institute> intersection = Sets.intersection(institutes, currentUserInstitutes());
                return (int) userRepository.countByInstitutesInAndType(intersection, UserType.HIWI);
            default:
                return 0;
        }
    }

    /**
     * @author Leon Chemnitz
     */
    @Override
    public int size(Query<User, String> query) {
        switch (currentUserType()) {
            case SUPERADMIN:
                return (int) userRepository.count();
            case ADMIN:
                return (int) userRepository.countByInstitutesIn(currentUserInstitutes());
            default:
                return 0;
        }
    }

    /**
     * @author Leon Chemnitz
     */
    @Override
    public Stream<User> fetch(Query<User, String> query) {
        Pageable pageable = new LimitOffsetPageRequest(query.getLimit(), query.getOffset());

        switch (currentUserType()) {
            case SUPERADMIN:
                return userRepository.findAll(pageable).stream();
            case ADMIN:
                return userRepository.findByInstitutesIn(currentUserInstitutes(), pageable);
            default:
                return Stream.empty();
        }
    }

    /**
     * 
     * @param query
     * @param filter
     * @return
     * @author Leon Chemnitz
     */
    public Stream<User> fetchHiwis(Query<User, String> query, Set<Institute> institutes) {
        Pageable pageable = new LimitOffsetPageRequest(query.getLimit(), query.getOffset());

        switch (currentUserType()) {
            case SUPERADMIN:
                return userRepository.findByInstitutesInAndType(institutes, UserType.HIWI, pageable);
            case ADMIN:
                Set<Institute> intersection = Sets.intersection(institutes, currentUserInstitutes());
                return userRepository.findByInstitutesInAndType(intersection, UserType.HIWI, pageable);
            default:
                return Stream.empty();
        }
    }
}