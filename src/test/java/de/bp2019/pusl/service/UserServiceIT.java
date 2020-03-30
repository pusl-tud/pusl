package de.bp2019.pusl.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.data.provider.Query;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bp2019.pusl.config.TestUtils;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * @author Leon Chemnitz
 */
@SpringBootTest
public class UserServiceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceIT.class);

    @Autowired
    TestUtils testUtils;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testDelete() throws Exception{
        LOGGER.info("testing delete");

        Institute institute = new Institute();
        institute.setName(RandomStringUtils.randomAlphanumeric(1,16));
        instituteRepository.save(institute);

        User user = new User(); 
        user.setEmailAddress(RandomStringUtils.randomAlphanumeric(1,16));
        user.setInstitutes(Sets.newSet(institute));


        LOGGER.info("testing as SUPERADMIN");        
        userRepository.deleteAll();        
        userRepository.save(user);
        testUtils.authenticateAs(UserType.SUPERADMIN);
        userService.delete(user);
        assertEquals(1, userRepository.count());
        
        LOGGER.info("testing as HIWI");
        userRepository.deleteAll();        
        userRepository.save(user);
        testUtils.authenticateAs(UserType.HIWI);
        assertThrows(UnauthorizedException.class, () -> userService.delete(user));
        assertEquals(2, userRepository.count());

        LOGGER.info("test successful");
    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testSave() throws Exception{
        LOGGER.info("testing save");

        Institute institute = new Institute();
        institute.setName(RandomStringUtils.randomAlphanumeric(1,16));
        instituteRepository.save(institute);

        User user = new User(); 
        user.setEmailAddress(RandomStringUtils.randomAlphanumeric(1,16));
        user.setInstitutes(Sets.newSet(institute));

        LOGGER.info("testing as SUPERADMIN");
        userRepository.deleteAll();
        testUtils.authenticateAs(UserType.SUPERADMIN);
        userService.save(user);
        assertEquals(2, userRepository.count());

        LOGGER.info("testing as ADMIN unauthorized");
        userRepository.deleteAll();
        User admin = testUtils.authenticateAs(UserType.ADMIN);
        assertThrows(UnauthorizedException.class, () -> userService.save(user));
        assertEquals(1, userRepository.count());

        LOGGER.info("testing as WIMI unauthorized");
        userRepository.deleteAll();
        User wimi = testUtils.createUser(UserType.WIMI);
        wimi.setInstitutes(Sets.newSet(institute));
        testUtils.authenticateAs(wimi);
        assertThrows(UnauthorizedException.class, () -> userService.save(user));
        assertEquals(1, userRepository.count());
        
        LOGGER.info("testing as HIWI unauthorized");
        userRepository.deleteAll();
        User hiwi = testUtils.createUser(UserType.HIWI);
        hiwi.setInstitutes(Sets.newSet(institute));
        testUtils.authenticateAs(hiwi);
        assertThrows(UnauthorizedException.class, () -> userService.save(user));
        assertEquals(1, userRepository.count());

        LOGGER.info("testing as ADMIN authorized");
        userRepository.deleteAll();
        admin.setInstitutes(Sets.newSet(institute));
        userRepository.save(admin);
        testUtils.authenticateAs(admin);
        userService.save(user);
        assertEquals(2, userRepository.count());
        
        LOGGER.info("test successful");
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testGetByIds() throws Exception {
        LOGGER.info("Testing getByIds");

        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setEmailAddress(RandomStringUtils.randomAlphanumeric(10));
        userRepository.save(user1);
        users.add(user1);

        User user2 = new User();
        user2.setEmailAddress(RandomStringUtils.randomAlphanumeric(10));
        userRepository.save(user2);
        users.add(user2);

        User user3 = new User();
        user3.setEmailAddress(RandomStringUtils.randomAlphanumeric(10));
        userRepository.save(user3);

        LOGGER.info("Testing null");
        assertEquals(0, userService.getByIds(null).size());

        LOGGER.info("Testing all Ids in DB");
        LOGGER.info("Users to get: " + users.toString());

        Set<ObjectId> ids = users.stream().map(User::getId).collect(Collectors.toSet());
        Set<User> fetchedUsers = userService.getByIds(ids);

        LOGGER.info("Users gotten: " + fetchedUsers.toString());
        TestUtils.assertCollectionsAreEqual(users, fetchedUsers);

        LOGGER.info("Testing not all Ids in DB");
        userRepository.delete(user2);
        fetchedUsers = userService.getByIds(ids);
        LOGGER.info("Users gotten: " + fetchedUsers.toString());

        TestUtils.assertCollectionsAreEqual(Arrays.asList(user1), fetchedUsers);
    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testFetchHiwis() throws Exception{
        LOGGER.info("Testing fetchHiwis");

        Institute institute = new Institute();
        institute.setName(RandomStringUtils.randomAlphanumeric(1,16));
        instituteRepository.save(institute);

        User user1 = new User();
        user1.setEmailAddress(RandomStringUtils.randomAlphanumeric(10));
        user1.setInstitutes(Sets.newSet(institute));
        user1.setType(UserType.HIWI);
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmailAddress(RandomStringUtils.randomAlphanumeric(10));
        user2.setInstitutes(Sets.newSet(institute));
        user2.setType(UserType.WIMI);
        userRepository.save(user2);

        User user3 = new User();
        user3.setEmailAddress(RandomStringUtils.randomAlphanumeric(10));
        user3.setType(UserType.HIWI);
        userRepository.save(user3);

        LOGGER.info("Testing as SUPERADMIN");
        testUtils.authenticateAs(UserType.SUPERADMIN);
        assertEquals(1, userService.fetchHiwis(new Query<>(), Sets.newSet(institute)).count());

        LOGGER.info("Testing as ADMIN");
        testUtils.authenticateAs(UserType.ADMIN);
        assertEquals(0, userService.fetchHiwis(new Query<>(), Sets.newSet(institute)).count());

        LOGGER.info("Testing as HIWI");
        testUtils.authenticateAs(UserType.HIWI);
        assertEquals(0, userService.fetchHiwis(new Query<>(), Sets.newSet(institute)).count());
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testSize() throws Exception {
        LOGGER.info("Testing size");

        Institute institute1 = new Institute(RandomStringUtils.randomAlphanumeric(8));
        instituteRepository.save(institute1);
        institute1 = instituteRepository.findByName(institute1.getName()).get();

        Institute institute2 = new Institute(RandomStringUtils.randomAlphanumeric(8));
        instituteRepository.save(institute2);
        institute2 = instituteRepository.findByName(institute2.getName()).get();

        User admin = testUtils.createUser(UserType.ADMIN);
        admin.setInstitutes(Sets.newSet(institute1));
        userRepository.save(admin);

        User user1 = new User();
        user1.setInstitutes(Sets.newSet(institute1));
        user1.setType(UserType.HIWI);
        userRepository.save(user1);

        User user2 = new User();
        user2.setInstitutes(Sets.newSet(institute2));
        user2.setType(UserType.HIWI);
        userRepository.save(user2);

        User user3 = new User();
        user3.setInstitutes(Sets.newSet(institute1, institute2));
        user3.setType(UserType.HIWI);
        userRepository.save(user3);

        testUtils.authenticateAs(admin);

        assertEquals(2, userService.sizeHiwis(new Query<>(), Sets.newSet(institute1)));
        assertEquals(3, userService.size(new Query<>()));
    }

}