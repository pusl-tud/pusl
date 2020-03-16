package de.bp2019.pusl.config;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.security.sasl.AuthenticationException;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;

/**
 * Utility Bean for testing. Includes things like authentication
 * 
 * @author Leon Chemnitz
 */
@Component
public class TestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TestProperties testProperties;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    private User superadmin;
    private String superadminPassword;
    private User admin;
    private String adminPassword;
    private User wimi;
    private String wimiPassword;
    private User hiwi;
    private String hiwiPassword;

    /**
     * Creates a random User of each {@link UserType} and saves it in the database.
     * 
     * @author Leon Chemnitz
     */
    @PostConstruct
    public void init() {
        LOGGER.info("creating superadmin user");
        superadmin = new User();
        superadmin.setType(UserType.SUPERADMIN);
        superadmin.setFirstName(RandomStringUtils.random(8, true, true));
        LOGGER.info("firstname: " + superadmin.getFirstName());
        superadmin.setLastName(RandomStringUtils.random(8, true, true));
        LOGGER.info("lastname: " + superadmin.getLastName());
        superadmin.setEmailAddress(RandomStringUtils.random(8, true, true) + "@"
                + RandomStringUtils.random(8, true, true) + "." + RandomStringUtils.random(4, true, false));
        LOGGER.info("email: " + superadmin.getEmailAddress());
        superadminPassword = RandomStringUtils.random(8, true, true);
        superadmin.setPassword(passwordEncoder.encode(superadminPassword));
        superadmin.setInstitutes(new HashSet<Institute>());
        userRepository.save(superadmin);

        Optional<User> foundUser = userRepository.findByEmailAddress(superadmin.getEmailAddress());
        if(foundUser.isPresent()){
            superadmin = foundUser.get();
        }else{
            LOGGER.error("Error with database couldn't create User");
            assertTrue(false);
        }

        LOGGER.info("creating admin user");
        admin = new User();
        admin.setType(UserType.ADMIN);
        admin.setFirstName(RandomStringUtils.random(8, true, true));
        LOGGER.info("firstname: " + admin.getFirstName());
        admin.setLastName(RandomStringUtils.random(8, true, true));
        LOGGER.info("lastname: " + admin.getLastName());
        admin.setEmailAddress(RandomStringUtils.random(8, true, true) + "@" + RandomStringUtils.random(8, true, true)
                + "." + RandomStringUtils.random(4, true, false));
        LOGGER.info("email: " + admin.getEmailAddress());
        adminPassword = RandomStringUtils.random(8, true, true);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setInstitutes(new HashSet<Institute>());
        userRepository.save(admin);

        foundUser = userRepository.findByEmailAddress(admin.getEmailAddress());
        if(foundUser.isPresent()){
            admin = foundUser.get();
        }else{
            LOGGER.error("Error with database couldn't create User");
            assertTrue(false);
        }

        LOGGER.info("creating wimi user");
        wimi = new User();
        wimi.setType(UserType.WIMI);
        wimi.setFirstName(RandomStringUtils.random(8, true, true));
        LOGGER.info("firstname: " + wimi.getFirstName());
        wimi.setLastName(RandomStringUtils.random(8, true, true));
        LOGGER.info("lastname: " + wimi.getLastName());
        wimi.setEmailAddress(RandomStringUtils.random(8, true, true) + "@" + RandomStringUtils.random(8, true, true)
                + "." + RandomStringUtils.random(4, true, false));
        LOGGER.info("email: " + wimi.getEmailAddress());
        wimiPassword = RandomStringUtils.random(8, true, true);
        wimi.setPassword(passwordEncoder.encode(wimiPassword));
        wimi.setInstitutes(new HashSet<Institute>());
        userRepository.save(wimi);

        foundUser = userRepository.findByEmailAddress(wimi.getEmailAddress());
        if(foundUser.isPresent()){
            wimi = foundUser.get();
        }else{
            LOGGER.error("Error with database couldn't create User");
            assertTrue(false);
        }


        LOGGER.info("creating hiwi user");
        hiwi = new User();
        hiwi.setType(UserType.HIWI);
        hiwi.setFirstName(RandomStringUtils.random(8, true, true));
        LOGGER.info("firstname: " + hiwi.getFirstName());
        hiwi.setLastName(RandomStringUtils.random(8, true, true));
        LOGGER.info("lastname: " + hiwi.getLastName());
        hiwi.setEmailAddress(RandomStringUtils.random(8, true, true) + "@" + RandomStringUtils.random(8, true, true)
                + "." + RandomStringUtils.random(4, true, false));
        LOGGER.info("email: " + hiwi.getEmailAddress());
        hiwiPassword = RandomStringUtils.random(8, true, true);
        hiwi.setPassword(passwordEncoder.encode(hiwiPassword));
        hiwi.setInstitutes(new HashSet<Institute>());
        userRepository.save(hiwi);
        
        foundUser = userRepository.findByEmailAddress(hiwi.getEmailAddress());
        if(foundUser.isPresent()){
            hiwi = foundUser.get();
        }else{
            LOGGER.error("Error with database couldn't create User");
            assertTrue(false);
        }
    }

    /**
     * Clears the Database again
     * 
     * @author Leon Chemnitz
     */
    @PreDestroy
    public void cleanUp() {
        LOGGER.info("cleaning user repository");
        userRepository.deleteAll();
    }

    /**
     * Return User object which was saved to database during initialization
     * 
     * @param type
     * @return
     * @throws DataNotFoundException
     * @author Leon Chemnitz
     */
    public User getUser(UserType type) throws DataNotFoundException {
        switch (type) {
            case SUPERADMIN:
                return superadmin;
            case ADMIN:
                return admin;
            case WIMI:
                return wimi;
            case HIWI:
                return hiwi;
            default:
                throw new DataNotFoundException();
        }
    }

    /**
     * Authenticate as a User of the given type
     * 
     * @param userType
     * @throws AuthenticationException
     * @author Leon Chemnitz
     */
    public void authenticateAs(UserType userType) throws AuthenticationException {
        SecurityContextHolder.clearContext();
        Authentication authentication;

        switch (userType) {
            case SUPERADMIN:
                LOGGER.info("authenticating as SUPERADMIN");
                authentication = new UsernamePasswordAuthenticationToken(superadmin.getEmailAddress(),
                        superadminPassword);
                break;
            case ADMIN:
                LOGGER.info("authenticating as ADMIN");
                authentication = new UsernamePasswordAuthenticationToken(admin.getEmailAddress(), adminPassword);
                break;
            case WIMI:
                LOGGER.info("authenticating as WIMI");
                authentication = new UsernamePasswordAuthenticationToken(wimi.getEmailAddress(), wimiPassword);
                break;
            case HIWI:
                LOGGER.info("authenticating as HIWI");
                authentication = new UsernamePasswordAuthenticationToken(hiwi.getEmailAddress(), hiwiPassword);
                break;
            default:
                throw new AuthenticationException("there were Errors during authentication");
        }

        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Assert that two Collections contain the same elements.
     * 
     * @param <T>
     * @param col1
     * @param col2
     * @author Leon Chemnitz
     */
    public static <T> void assertCollectionsAreEqual(Collection<T> col1, Collection<T> col2) {
        col1.forEach(item -> assertTrue(col2.contains(item)));
        col2.forEach(item -> assertTrue(col1.contains(item)));
    }
}