package de.bp2019.pusl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;

import com.vaadin.flow.data.provider.Query;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bp2019.pusl.config.TestUtils;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.LectureRepository;
import de.bp2019.pusl.repository.UserRepository;

/**
 * Tests for {@link GradeService}
 * 
 * @author Leon Chemnitz
 */
@SpringBootTest
public class GradeServiceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradeServiceIT.class);

    @Autowired
    TestUtils testUtils;

    @Autowired
    GradeService gradeService;

    @Autowired
    InstituteService instituteService;

    @Autowired
    InstituteRepository instituteRepository;
    
    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void cleanUp() {
        instituteRepository.deleteAll();
        gradeRepository.deleteAll();
    }

    @Test
    public void testSize() throws Exception{
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

        Lecture lecture1 = new Lecture();
        lecture1.setName(RandomStringUtils.randomAlphanumeric(8));
        lecture1.setInstitutes(Sets.newSet(institute1));
        Exercise exercise11 = new Exercise();
        exercise11.setName(RandomStringUtils.randomAlphanumeric(8));
        Exercise exercise12 = new Exercise();
        exercise12.setName(RandomStringUtils.randomAlphanumeric(8));
        lecture1.setExercises(Arrays.asList(exercise11, exercise12));
        lectureRepository.save(lecture1);
        lecture1 = lectureRepository.findByName(lecture1.getName()).get();

        Lecture lecture2 = new Lecture();
        lecture2.setName(RandomStringUtils.randomAlphanumeric(8));
        lecture2.setInstitutes(Sets.newSet(institute2));
        Exercise exercise21 = new Exercise();
        exercise21.setName(RandomStringUtils.randomAlphanumeric(8));
        Exercise exercise22 = new Exercise();
        exercise22.setName(RandomStringUtils.randomAlphanumeric(8));
        lecture2.setExercises(Arrays.asList(exercise21, exercise22));
        lectureRepository.save(lecture2);
        lecture2 = lectureRepository.findByName(lecture2.getName()).get();


        testUtils.authenticateAs(UserType.ADMIN);

        Grade grade1, grade2, grade3, grade4;


        LOGGER.info("testing institute matching");

        grade1 = new Grade(lecture1, exercise11, "", "", LocalDate.now());
        gradeRepository.save(grade1);

        grade2 = new Grade(lecture1, exercise12, "", "", LocalDate.now());
        gradeRepository.save(grade2);

        grade3 = new Grade(lecture2, exercise21, "", "", LocalDate.now());
        gradeRepository.save(grade3);

        grade4 = new Grade(lecture2, exercise22, "", "", LocalDate.now());
        gradeRepository.save(grade4);

        
        assertEquals(2 , gradeService.size(new Query<>()));
    }
}