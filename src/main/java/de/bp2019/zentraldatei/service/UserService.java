package de.bp2019.zentraldatei.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.User;

/**
 * Service providing relevant Users
 * @author Leon Chemnitz
 */
@Service
public class UserService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InstituteService.class);

    List<User> allUsers;

    public UserService() {
        LOGGER.debug("Started  creation of InstituteService");

        /* Platzhalter Code da noch keine echten Repositories existieren */
        allUsers = new ArrayList<User>();

        allUsers.add(new User("Walter", "Frosch", null, null, null, null));
        allUsers.add(new User("Peter", "Pan", null, null, null, null));
        allUsers.add(new User("Angela", "Merkel", null, null, null, null));
        allUsers.add(new User("John", "Lennon", null, null, null, null));
        allUsers.add(new User("Helene", "Fischer", null, null, null, null));
        allUsers.add(new User("Walter", "Gropius", null, null, null, null));

        LOGGER.debug("Finished creation of InstituteService");
    }

    public List<User> getAllUsers() {
        return allUsers;
    }
}