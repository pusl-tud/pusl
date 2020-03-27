package de.bp2019.pusl.config;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.security.sasl.AuthenticationException;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
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
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;

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

    private Map<ObjectId, String> idToPassword = new HashMap<>();

    /**
     * Clears the Database
     * 
     * @author Leon Chemnitz
     */
    @PreDestroy
    public void cleanUp() {
        LOGGER.info("cleaning user repository");
        userRepository.deleteAll();
    }

    /**
     * 
     * @param userType
     * @return
     * @author Leon Chemnitz
     */
    public User createUser(UserType userType) {
        return createUser(userType, RandomStringUtils.randomAlphanumeric(14));
    }

    public String getPasswordOfUser(User user){
        return idToPassword.get(user.getId());
    }

    /**
     * Create a User and save it to the database
     * 
     * @param userType
     * @return
     * @author Leon Chemnitz
     */
    public User createUser(UserType userType, String password) {
        User user = new User();
        user.setFirstName(RandomStringUtils.randomAlphanumeric(14));
        user.setLastName(RandomStringUtils.randomAlphanumeric(14));
        user.setEmailAddress(RandomStringUtils.randomAlphanumeric(14) + "@" + RandomStringUtils.randomAlphanumeric(14)
                + "." + RandomStringUtils.randomAlphabetic(5));
        user.setType(userType);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
        idToPassword.put(user.getId(), password);

        LOGGER.info("generated User: " + user.toString());
        LOGGER.info("password: " + password);

        return user;
    }

    /**
     * Authenticate as User previously created by createUser
     * 
     * @param user
     * @return
     * @author Leon Chemnitz
     */
    public User authenticateAs(User user) {
        LOGGER.info("authenticating as " + user.toString());

        SecurityContextHolder.clearContext();
        Authentication authentication;

        String password = idToPassword.get(user.getId());

        authentication = new UsernamePasswordAuthenticationToken(user.getEmailAddress(), password);

        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }

    /**
     * Create User of given Type and authenticate as it
     * 
     * @param userType
     * @throws AuthenticationException
     * @author Leon Chemnitz
     */
    public User authenticateAs(UserType userType) throws AuthenticationException {
        LOGGER.info("authenticating as " + userType.toString());

        SecurityContextHolder.clearContext();
        Authentication authentication;

        String password = RandomStringUtils.randomAlphanumeric(14);
        User user = createUser(userType, password);

        authentication = new UsernamePasswordAuthenticationToken(user.getEmailAddress(), password);

        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
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