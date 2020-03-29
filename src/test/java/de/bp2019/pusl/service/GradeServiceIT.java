package de.bp2019.pusl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.LectureRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

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
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void cleanUp() {
        instituteRepository.deleteAll();
        gradeRepository.deleteAll();
    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testDelete() throws Exception{
        LOGGER.info("Tetsing delete");

        Institute institute = new Institute();
        institute.setName(RandomStringUtils.randomAlphanumeric(1, 16));
        instituteRepository.save(institute);
            
        Token token1 = new Token();
        token1.setName(RandomStringUtils.randomAlphanumeric(1,16));
            
        Token token2 = new Token();
        token2.setName(RandomStringUtils.randomAlphanumeric(1,16));

        ExerciseScheme exerciseScheme = new ExerciseScheme();
        exerciseScheme.setName(RandomStringUtils.randomAlphanumeric(1, 16));
        exerciseScheme.setTokens(Sets.newSet(token2,token1));
        exerciseScheme.setDefaultValueToken(token1);
        exerciseSchemeRepository.save(exerciseScheme);

        Exercise exercise = new Exercise();
        exercise.setName(RandomStringUtils.randomAlphanumeric(1,16));

        Lecture lecture = new Lecture();
        lecture.setInstitutes(Sets.newSet(institute));
        lecture.setName(RandomStringUtils.randomAlphanumeric(1,16));
        lecture.setExercises(Arrays.asList(exercise));

        Grade grade = new Grade();
        grade.setMatrNumber(RandomStringUtils.randomNumeric(7));
        grade.setLecture(lecture);
        grade.setExercise(exercise);

        gradeRepository.deleteAll();
        gradeRepository.save(grade);

        LOGGER.info("testing as SUPERADMIN");
        testUtils.authenticateAs(UserType.SUPERADMIN);
        gradeService.delete(grade);
        assertEquals(0, gradeRepository.count());

        LOGGER.info("test successful");
    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testSave() throws Exception{
        LOGGER.info("Tetsing save");

        Institute institute = new Institute();
        institute.setName(RandomStringUtils.randomAlphanumeric(1, 16));
        instituteRepository.save(institute);
            
        Token token1 = new Token();
        token1.setName(RandomStringUtils.randomAlphanumeric(1,16));
            
        Token token2 = new Token();
        token2.setName(RandomStringUtils.randomAlphanumeric(1,16));

        ExerciseScheme exerciseScheme = new ExerciseScheme();
        exerciseScheme.setName(RandomStringUtils.randomAlphanumeric(1, 16));
        exerciseScheme.setTokens(Sets.newSet(token2,token1));
        exerciseScheme.setDefaultValueToken(token1);
        exerciseSchemeRepository.save(exerciseScheme);

        Exercise exercise = new Exercise();
        exercise.setName(RandomStringUtils.randomAlphanumeric(1,16));

        Lecture lecture = new Lecture();
        lecture.setInstitutes(Sets.newSet(institute));
        lecture.setName(RandomStringUtils.randomAlphanumeric(1,16));
        lecture.setExercises(Arrays.asList(exercise));

        Grade grade = new Grade();
        grade.setMatrNumber(RandomStringUtils.randomNumeric(7));
        grade.setLecture(lecture);
        grade.setExercise(exercise);

        LOGGER.info("testing as SUPERADMIN");
        gradeRepository.deleteAll();
        testUtils.authenticateAs(UserType.SUPERADMIN);
        gradeService.save(grade);
        assertEquals(1, gradeRepository.count());
        
        LOGGER.info("testing as ADMIN unauthorized");
        gradeRepository.deleteAll();
        User admin = testUtils.authenticateAs(UserType.ADMIN);
        assertThrows(UnauthorizedException.class, () -> gradeService.save(grade));
        assertEquals(0, gradeRepository.count());

        LOGGER.info("testing as ADMIN authorized");
        gradeRepository.deleteAll();
        admin.setInstitutes(Sets.newSet(institute));
        userRepository.save(admin);
        testUtils.authenticateAs(admin);
        gradeService.save(grade);
        assertEquals(1, gradeRepository.count());
        
        LOGGER.info("testing as WIMI unauthorized");
        gradeRepository.deleteAll();
        User wimi = testUtils.authenticateAs(UserType.WIMI);
        assertThrows(UnauthorizedException.class, () -> gradeService.save(grade));
        assertEquals(0, gradeRepository.count());
        
        LOGGER.info("testing as WIMI authorized");
        gradeRepository.deleteAll();
        wimi.setInstitutes(Sets.newSet(institute));
        userRepository.save(wimi);
        testUtils.authenticateAs(wimi);
        gradeService.save(grade);
        assertEquals(1, gradeRepository.count());

        LOGGER.info("testing as HIWI not authorized");
        gradeRepository.deleteAll();
        User hiwi = testUtils.authenticateAs(UserType.HIWI);
        assertThrows(UnauthorizedException.class, () -> gradeService.save(grade));
        assertEquals(0, gradeRepository.count());
        
        LOGGER.info("testing HIWI authorized");
        gradeRepository.deleteAll();
        hiwi.setInstitutes(Sets.newSet(institute));
        userRepository.save(hiwi);
        testUtils.authenticateAs(hiwi);
        exercise.setAssignableByHIWI(true);
        lecture.setHasAccess(Sets.newSet(hiwi.getId()));
        grade.setLecture(lecture);
        grade.setExercise(exercise);
        testUtils.authenticateAs(hiwi);
        gradeService.save(grade);
        assertEquals(1, gradeRepository.count());

        LOGGER.info("test successful");
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

        User admin = testUtils.createUser(UserType.ADMIN);
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


        testUtils.authenticateAs(admin);

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