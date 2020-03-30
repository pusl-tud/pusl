package de.bp2019.pusl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.Query;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;
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
@Scope("session")
public class UserService extends AbstractDataProvider<User, String> {
    private static final long serialVersionUID = 1866448855648692985L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    AuthenticationService authenticationService;

    /**
     * Save one User
     *
     * @param user to persist
     * @author Leon Chemnitz
     * @throws UnauthorizedException if user not authorized to access
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
     * @throws UnauthorizedException if user not authorized to access
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
     * @throws DataNotFoundException if entity not found in database
     * @throws UnauthorizedException if user not authorized to access
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

    public Set<User> getByIds(Set<ObjectId> ids){
        if(ids == null) {
            LOGGER.debug("getting Users by ID but parameter was null");
            return new HashSet<>();
        }       
        LOGGER.debug("getting Users with IDs: " + ids.toString());

        Iterable<String> idsString = ids.stream().map(ObjectId::toString).collect(Collectors.toList());
        Iterable<User> users = userRepository.findAllById(idsString);

        Set<User> userSet = new HashSet<>();

        users.forEach(u -> userSet.add(u));

        return userSet;
    }

    /**
     * Check if current user is authorized to access the {@link User}
     * 
     * @param user entity to check
     * @return true if authorized
     * @author Leon Chemnitz
     */
    private boolean userIsAuthorized(User user) {
        User currentUser = authenticationService.currentUser();

        /* everybody is allowed to edit themselves */
        if(user.getId() != null && user.getId().equals(currentUser.getId())){
            return true;
        }

        switch (currentUser.getType()) {
            default:
            case HIWI:
            case WIMI:
                break;
            case ADMIN:
                if (!Utils.containsAny(currentUser.getInstitutes(), user.getInstitutes()))
                    break;
                return true;
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
     * @param email email to check
     * @param id id of current user
     * @return true if available
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
     * @return list of user types
     * @author Leon Chemnitz
     */
    public List<UserType> getUserTypes() {
        UserType currentUserType = authenticationService.currentUserType();

        switch (currentUserType) {
            case SUPERADMIN:
                return Arrays.asList(UserType.values());
            case ADMIN:
                return Arrays.asList(UserType.ADMIN, UserType.WIMI, UserType.HIWI);
            default:
                return new ArrayList<>();
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
     * @param query Query
     * @param institutes query filter
     * @return num of hiwis
     * @author Leon Chemnitz
     */
    public int sizeHiwis(Query<User, String> query, Set<Institute> institutes) {
        User currentUser = authenticationService.currentUser();

        switch (currentUser.getType()) {
            case SUPERADMIN:
                return (int) userRepository.countByInstitutesInAndType(institutes, UserType.HIWI);
            case ADMIN:
                Set<Institute> intersection = Sets.intersection(institutes, currentUser.getInstitutes());
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
        User currentUser = authenticationService.currentUser();

        switch (currentUser.getType()) {
            case SUPERADMIN:
                return (int) userRepository.count();
            case ADMIN:
                return (int) userRepository.countByInstitutesIn(currentUser.getInstitutes());
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
        User currentUser = authenticationService.currentUser();

        switch (currentUser.getType()){
            case SUPERADMIN:
                return userRepository.findAll(pageable).stream();
            case ADMIN:
                return userRepository.findByInstitutesIn(currentUser.getInstitutes(), pageable);
            default:
                return Stream.empty();
        }
    }

    /**
     * 
     * @param query Query
     * @param institutes query filter
     * @return stream of fetched users
     * @author Leon Chemnitz
     */
    public Stream<User> fetchHiwis(Query<User, String> query, Set<Institute> institutes) {
        Pageable pageable = new LimitOffsetPageRequest(query.getLimit(), query.getOffset());
        User currentUser = authenticationService.currentUser();

        switch (currentUser.getType()) {
            case SUPERADMIN:
                return userRepository.findByInstitutesInAndType(institutes, UserType.HIWI, pageable);
            case ADMIN:
                Set<Institute> intersection = Sets.intersection(institutes, currentUser.getInstitutes());
                return userRepository.findByInstitutesInAndType(intersection, UserType.HIWI, pageable);
            default:
                return Stream.empty();
        }
    }
}