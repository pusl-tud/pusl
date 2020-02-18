package de.bp2019.pusl.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;

/**
 * Service providing relevant Users. Implements {@link UserDetailsService} to be
 * used with Spring Security
 * 
 * @author Leon Chemnitz
 */
@Service
public class UserService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmailAddress(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    /**
     * Returns the currently logged in User as a {@link User} Object
     * 
     * @return current User
     * @author Leon Chemnitz
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmailAddress(email);
    }

    /**
     * Returns the Full name of the currently logged in User. Returns the users
     * email address if no firstname is set
     * 
     * @return Full name of current user
     * @author Leon Chemnitz
     */
    public String getCurrentUserFullName() {
        return getFullName(getCurrentUser());
    }

    /**
     * @return Type of current User
     * @author Leon Chemnitz
     */
    public UserType getCurrentUserType() {
        return getCurrentUser().getType();
    }

    /**
     * Get all Users the User is authenticated to see.
     * 
     * @return list of all users
     * @author Leon Chemnitz
     */
    public List<User> getAll() {
        // TODO: implement authentication
        return userRepository.findAll();
    }

    /**
     * Get the full name of a user.  Returns the users
     * email address if no firstname is set
     * 
     * @param user User
     * @return Full name of the found User as a String. null if no user is found
     * @author Leon Chemnitz
     */
    public static String getFullName(User user) {
        if (user != null) {
            /* initial admin has no name */
            if (user.getFirstName() == null) {
                return user.getEmailAddress();
            }
            return user.getFirstName() + " " + user.getLastName();
        } else {
            return null;
        }
    }

    /**
     * Delete a User
     *
     * @param user to delete
     * @author Leon Chemnitz
     */
    public void delete(User user) {
        // TODO: Implement Authentication
        userRepository.delete(user);
    }

    /**
     * Save one User
     *
     * @param user to Save
     * @author Leon Chemnitz
     */
    public void save(User user) {
        // TODO: Implement Authentication
        userRepository.save(user);
    }

    /**
     * Get a User based on its Id. Only returns Users the active User is
     * authenticated to see.
     * 
     * @param id Id to search for
     * @return found User with matching Id, null if none is found
     * @author Leon Chemnitz
     */
    public User getById(String id) {
        // TODO: implement authentication
        Optional<User> foundUser = userRepository.findById(id);

        if (foundUser.isPresent()) {
            return foundUser.get();
        } else {
            LOGGER.warn("Tried to get User which doesn't exist in Database! User ID was: " + id);
            return null;
        }
    }

    public List<UserType> getUserTypes() {
        return Arrays.asList(UserType.values());
    }

    public UserService() {
    }
}