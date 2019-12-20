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
    
    public String getFullNameById(String id) {
        // TODO: authentication
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user.get().getFirstName() + " " + user.get().getLastName();
        } else {
            LOGGER.warn("Tried to find User which doesn't exist in Database! User Id was: " + id);
            return null;
        }
    }
    
    // einfach mal hier eingefügt
    /**
     * Save the user and all its data
     * 
     * @param user
     * @return returns the userrepository with the new user
     * @author Fabio Costa
     */
    public User saveUser(User user){
    	// TODO: authentication
    	return userRepository.save(user);
    }

    
    /**
     * Searches after specific user id
     * 
     * @param user
     * @return returns the user
     * @author Fabio Costa
     */
	public User getUserID(String userId) {
		Optional<User> user = userRepository.findById(userId);
		
		if (user.isPresent()) {
			return user.get();
		} else {
			LOGGER.warn("Tried to find User which doesn't exist in Database! User Id was: " + userId);
            return null;
		}
	}

	/**
     * Deletes a specific user
     * 
     * @param user
     * @author Fabio Costa
     */
	public void deleteUser(User user) {
		userRepository.delete(user);
		
	}

	
}