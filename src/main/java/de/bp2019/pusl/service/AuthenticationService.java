package de.bp2019.pusl.service;

import java.util.Optional;
import java.util.Set;

import com.vaadin.flow.component.UI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.views.LoginView;

/**
 * Service providing current authentication details. Bean is stateful to improve
 * performance. The authenticated {@link User} Object is kept in session memory
 * so that it doesnt have to be looked up from the database every time
 * authentication details are requested
 * 
 * @author Leon Chemnitz
 */
@Service
@Scope("session")
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    private Optional<User> currentUser = Optional.empty();

    /**
     * Returns the currently logged in User as a {@link User} Object.
     * 
     * @return current User
     * @author Leon Chemnitz
     */
    public User currentUser() {
        LOGGER.debug("getting current user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            LOGGER.debug("User not authenticated");
            currentUser = Optional.empty();
        } else {
            String email = authentication.getName();

            if (currentUser.isEmpty() || !currentUser.get().getEmailAddress().equals(email)) {
                LOGGER.debug("getting user from database. Email is: " + email);

                currentUser = userRepository.findByEmailAddress(email);

                LOGGER.debug(currentUser.toString());
            }
        }

        if (currentUser.isPresent()) {
            return currentUser.get();
        } else {
            return new User();
        }
    }


    /**
     * Clear the cached version of {@link User} object stored in memory. Forces a
     * Reload from Database
     * 
     * @author Leon Chemnitz
     */
    public void clearCache() {
        LOGGER.info("clearing authentication cache");
        currentUser = Optional.empty();
    }

    /**
     * Deauthenticate the currently logged in user and navigate to login view
     * 
     * @author Leon Chemnitz
     */
    public void deauthenticate() {
        /* Clear the Spring Authentication */
        SecurityContextHolder.clearContext();
        /* Close the VaadinServiceSession */
        UI.getCurrent().getSession().close();
        /* Redirect to avoid keeping the removed UI open in the browser */
        UI.getCurrent().navigate(LoginView.ROUTE);
    }

    /**
     * Returns the Full name of the currently logged in User. Returns the users
     * email address if no firstname is set
     * 
     * @return Full name of current user
     * @author Leon Chemnitz
     */
    public String currentUserFullName() {
        return currentUser().getFullName();
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

}