package de.bp2019.pusl.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import de.bp2019.pusl.config.TestUtils;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * Tests for {@link InstituteService}
 * 
 * @author Leon Chemnitz
 */
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class InstituteServiceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstituteServiceIT.class);

    @Autowired
    TestUtils testUtils;

    @Autowired
    InstituteService instituteService;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void cleanUp() {
        instituteRepository.deleteAll();
    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testGetById() throws Exception {
        LOGGER.info("Testing getById");

        Institute institute1 = instituteRepository.save(new Institute(RandomStringUtils.randomAlphanumeric(8)));
        Institute institute2 = instituteRepository.save(new Institute(RandomStringUtils.randomAlphanumeric(8)));

        User admin = testUtils.createUser(UserType.ADMIN);
        Set<ObjectId> instituteSet = new HashSet<>();
        instituteSet.add(institute2.getId());
        admin.setInstitutes(instituteSet);
        userRepository.save(admin);

        testUtils.authenticateAs(UserType.SUPERADMIN);
        LOGGER.info("testing invalid id");
        assertThrows(DataNotFoundException.class, () -> instituteService.getById(ObjectId.get()));
        LOGGER.info("testing SUPERADMIN authorized correct id");
        assertEquals(institute1, instituteService.getById(institute1.getId()));

        testUtils.authenticateAs(admin);
        LOGGER.info("testing ADMIN unauthorized");
        assertThrows(UnauthorizedException.class, () -> instituteService.getById(institute1.getId()));
        LOGGER.info("testing ADMIN authorized correct id");
        assertEquals(institute2, instituteService.getById(institute2.getId()));
    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testSave() throws Exception {
        LOGGER.info("Testing save");

        Institute institute = new Institute(RandomStringUtils.randomAlphanumeric(8));

        LOGGER.info("saving as SUPERADMIN");
        testUtils.authenticateAs(UserType.SUPERADMIN);
        instituteService.save(institute);
        assertTrue(instituteRepository.findByName(institute.getName()).isPresent());
        instituteRepository.deleteAll();

        LOGGER.info("saving as ADMIN");
        testUtils.authenticateAs(UserType.ADMIN);
        assertThrows(UnauthorizedException.class, () -> instituteService.save(institute));
        assertFalse(instituteRepository.findByName(institute.getName()).isPresent());

        LOGGER.info("saving as WIMI");
        testUtils.authenticateAs(UserType.WIMI);
        assertThrows(UnauthorizedException.class, () -> instituteService.save(institute));
        assertFalse(instituteRepository.findByName(institute.getName()).isPresent());

        LOGGER.info("saving as HIWI");
        testUtils.authenticateAs(UserType.HIWI);
        assertThrows(UnauthorizedException.class, () -> instituteService.save(institute));
        assertFalse(instituteRepository.findByName(institute.getName()).isPresent());
    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testDelete() throws Exception {
        LOGGER.info("Testing deletion of institute");

        Institute institute = new Institute(RandomStringUtils.randomAlphanumeric(8));
        instituteRepository.save(institute);

        LOGGER.info("deleting as SUPERADMIN");
        testUtils.authenticateAs(UserType.SUPERADMIN);
        instituteService.delete(institute);
        assertFalse(instituteRepository.findByName(institute.getName()).isPresent());

        instituteRepository.save(institute);

        LOGGER.info("saving as ADMIN");
        testUtils.authenticateAs(UserType.ADMIN);
        assertThrows(UnauthorizedException.class, () -> instituteService.delete(institute));
        assertTrue(instituteRepository.findByName(institute.getName()).isPresent());

        LOGGER.info("saving as WIMI");
        testUtils.authenticateAs(UserType.WIMI);
        assertThrows(UnauthorizedException.class, () -> instituteService.delete(institute));
        assertTrue(instituteRepository.findByName(institute.getName()).isPresent());

        LOGGER.info("saving as HIWI");
        testUtils.authenticateAs(UserType.HIWI);
        assertThrows(UnauthorizedException.class, () -> instituteService.delete(institute));
        assertTrue(instituteRepository.findByName(institute.getName()).isPresent());
    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testCheckNameAvailable() {
        LOGGER.info("Testing checkNameaAvailable");

        Institute institute1 = instituteRepository.save(new Institute(RandomStringUtils.randomAlphanumeric(8)));
        Institute institute2 = instituteRepository.save(new Institute(RandomStringUtils.randomAlphanumeric(8)));

        LOGGER.info("name is available");
        assertTrue(instituteService.checkNameAvailable(RandomStringUtils.randomAlphanumeric(7),
                Optional.of(institute1.getId())));

        LOGGER.info("same name as self");
        assertTrue(instituteService.checkNameAvailable(institute1.getName(), Optional.of(institute1.getId())));

        LOGGER.info("name in use id set");
        assertFalse(instituteService.checkNameAvailable(institute2.getName(), Optional.of(institute1.getId())));

        LOGGER.info("name in use id empty");
        assertFalse(instituteService.checkNameAvailable(institute2.getName(), Optional.empty()));
    }

}