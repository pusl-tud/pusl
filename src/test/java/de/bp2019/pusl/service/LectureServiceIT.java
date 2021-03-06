package de.bp2019.pusl.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import de.bp2019.pusl.config.TestUtils;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.LectureRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * @author Leon Chemnitz
 */
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class LectureServiceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(LectureServiceIT.class);


    @Autowired
    TestUtils testUtils;

    @Autowired
    InstituteService instituteService;

    @Autowired
    LectureService lectureService;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testDelete() throws Exception{
        LOGGER.info("testing delete");

        Institute institute = new Institute();
        institute.setName(RandomStringUtils.randomAlphanumeric(1,16));
        instituteRepository.save(institute);

        Lecture lecture = new Lecture();
        lecture.setName(RandomStringUtils.randomAlphanumeric(8));
        lecture.setInstitutes(Sets.newSet(institute.getId()));


        LOGGER.info("testing as SUPERADMIN");        
        lectureRepository.deleteAll();        
        lectureRepository.save(lecture);
        testUtils.authenticateAs(UserType.SUPERADMIN);
        lectureService.delete(lecture);
        assertEquals(0, lectureRepository.count());
        
        LOGGER.info("testing as HIWI");
        lectureRepository.deleteAll();        
        lectureRepository.save(lecture);
        testUtils.authenticateAs(UserType.HIWI);
        assertThrows(UnauthorizedException.class, () -> lectureService.delete(lecture));
        assertEquals(1, lectureRepository.count());

        LOGGER.info("test successful");
    }
    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testSave() throws Exception {
        LOGGER.info("Testing save");

        Institute institute1 = new Institute(RandomStringUtils.randomAlphanumeric(8));
        instituteRepository.save(institute1);
        Institute institute2 = new Institute(RandomStringUtils.randomAlphanumeric(8));
        instituteRepository.save(institute2);

        Lecture lecture = new Lecture();
        lecture.setName(RandomStringUtils.randomAlphanumeric(8));
        lecture.setInstitutes(Sets.newSet(institute1.getId(), institute2.getId()));

        LOGGER.info("saving as SUPERADMIN");
        testUtils.authenticateAs(UserType.SUPERADMIN);
        lectureService.save(lecture);
        assertTrue(lectureRepository.findByName(lecture.getName()).isPresent());
        lectureRepository.deleteAll();

        LOGGER.info("saving as ADMIN unauthorized");
        User admin = testUtils.authenticateAs(UserType.ADMIN);
        assertThrows(UnauthorizedException.class, () -> lectureService.save(lecture));
        assertFalse(lectureRepository.findByName(lecture.getName()).isPresent());

        admin.setInstitutes(Sets.newSet(institute1.getId()));
        userRepository.save(admin);

        testUtils.authenticateAs(UserType.WIMI);
        LOGGER.info("saving as WIMI");
        testUtils.authenticateAs(UserType.WIMI);
        assertThrows(UnauthorizedException.class, () -> lectureService.save(lecture));
        assertFalse(lectureRepository.findByName(lecture.getName()).isPresent());

        LOGGER.info("saving as ADMIN authorized");
        testUtils.authenticateAs(admin);
        lectureService.save(lecture);
        assertTrue(lectureRepository.findByName(lecture.getName()).isPresent());
        lectureRepository.deleteAll();

        LOGGER.info("saving as HIWI");
        testUtils.authenticateAs(UserType.HIWI);
        assertThrows(UnauthorizedException.class, () -> lectureService.save(lecture));
        assertFalse(lectureRepository.findByName(lecture.getName()).isPresent());
    }

}