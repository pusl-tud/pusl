package de.bp2019.pusl.service;

import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;

/**
 * Ths Creation Service is used to add the first admin to the Database, when the
 * Application is started for the first time. The first user has emailAddres
 * admin and password admin and is only added if no other users exist in the
 * database
 * 
 * @author Leon Chemnitz
 */
@Service
public class CreationService {
    
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.findAll().size() == 0) {
            User admin = new User();
            admin.setEmailAddress("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setType(UserType.SUPERADMIN);
            admin.setInstitutes(new HashSet<ObjectId>());
            userRepository.save(admin);
        }
    }
}