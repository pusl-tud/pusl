package de.bp2019.pusl.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;

/**
 * Service providing relevant Users
 * 
 * @author Leon Chemnitz
 */
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    public UserService() {
    }

    /**
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
     * 
     * @return Full name of current user
     * @author Leon Chemnitz
     */
    public String getCurrentUserFullName() {
        return getFullName(getCurrentUser());
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
     * Get the full name of a user
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
}