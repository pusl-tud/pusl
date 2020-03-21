package de.bp2019.pusl.service;

import static org.junit.Assert.assertEquals;

import com.vaadin.flow.data.provider.Query;

import org.apache.commons.lang3.RandomStringUtils;
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

    @Test
    public void testSize() throws Exception {
        LOGGER.info("Testing size");

        Institute institute1 = new Institute(RandomStringUtils.randomAlphanumeric(8));
        instituteRepository.save(institute1);
        institute1 = instituteRepository.findByName(institute1.getName()).get();

        Institute institute2 = new Institute(RandomStringUtils.randomAlphanumeric(8));
        instituteRepository.save(institute2);
        institute2 = instituteRepository.findByName(institute2.getName()).get();

        User admin = testUtils.getUser(UserType.ADMIN);
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

        testUtils.authenticateAs(UserType.ADMIN);

        assertEquals(2, userService.sizeHiwis(new Query<>(), Sets.newSet(institute1)));
        assertEquals(3, userService.size(new Query<>()));
    }

}