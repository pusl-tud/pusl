package de.bp2019.zentraldatei.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.repository.UserRepository;

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
     * Get all Users the User is authenticated to see.
     * 
     * @return list of all users
     * @author Leon Chemnitz
     */
    public List<User> getAllUsers() {
        // TODO: implement authentication
        return userRepository.findAll();
    }

    /**
     * Get just the Ids of all the Users the User is authenticated to see.
     * 
     * @return list of all user ids
     * @author Leon Chemnitz
     */
    public List<String> getAllUserIDs() {
        // TODO: authentication
        return userRepository.findAll().stream().map(User::getId).collect(Collectors.toList());
    }

    /**
     * Get the full name of a user based on their ID.
     * 
     * @param id User.id
     * @return Full name of the found User as a String. null if no user is found
     * @author Leon Chemnitz
     */
    public static String getFullName(User user) {
        if (user != null) {
            return user.getFirstName() + " " + user.getLastName();
        } else {
            return null;
        }
    }

    /**
     * Get a User based on its Id. Only return when a User isPressent
     * 
     * @param userid Id to search for
     * @return found user with matching id, else return null 
     * @author Fabio Costa
     */
	public Optional<User> getUserID(String userId) {

		return userRepository.findById(userId);
	}

	/**
     * Save a User
     *
     * @param user to save
     * @author Fabio Costa
     */
	public void saveUser(User user) {
		
	    userRepository.save(user);
	}

	/**
     * Delete a User
     *
     * @param user to delete
     * @author Fabio Costa
     */
	public void deleteUser(User user) {
		
		userRepository.delete(user);
	}
}