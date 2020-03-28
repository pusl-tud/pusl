package de.bp2019.pusl.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bp2019.pusl.config.TestUtils;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Performance;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.repository.LectureRepository;

@SpringBootTest
public class CalculationServiceIT {    
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationServiceIT.class);

    @Autowired
    TestUtils testUtils;

    @Autowired
    CalculationService calculationService;

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Test
    public void calculatePerformances() throws Exception{
        LOGGER.info("testing calculatePerformances");

        testUtils.authenticateAs(UserType.SUPERADMIN);

        ExerciseScheme exerciseScheme = new ExerciseScheme();
        exerciseScheme.setName(RandomStringUtils.randomAlphanumeric(16));
        exerciseScheme.setIsNumeric(false);
        exerciseSchemeRepository.save(exerciseScheme);

        Exercise exercise = new Exercise();
        exercise.setName(RandomStringUtils.randomAlphanumeric(16));
        exercise.setScheme(exerciseScheme);

        PerformanceScheme performanceScheme = new PerformanceScheme();
        performanceScheme.setName(RandomStringUtils.randomAlphanumeric(16));

        String calculationRule = "function calculate(results) { \n";
        calculationRule += "if(results[0] == 'x') { \n";
        calculationRule += "   return 'a'; \n";
        calculationRule += "} else {  \n";
        calculationRule += "   return 'b'; \n";
        calculationRule += " } \n";
        calculationRule += "} \n";

        performanceScheme.setCalculationRule(calculationRule);
                
        Lecture lecture = new Lecture();
        lecture.setName(RandomStringUtils.randomAlphanumeric(16));
        lecture.setExercises(Arrays.asList(exercise));
        lecture.setPerformanceSchemes(Arrays.asList(performanceScheme));
        lectureRepository.save(lecture);
        
        Grade grade1 = new Grade();
        grade1.setLecture(lecture);
        grade1.setExercise(exercise);
        String matr1 = RandomStringUtils.randomNumeric(7);
        grade1.setMatrNumber(matr1);
        grade1.setValue("x");
        gradeRepository.save(grade1);
        
        Grade grade2 = new Grade();
        grade2.setLecture(lecture);
        grade2.setExercise(exercise);
        String matr2 = RandomStringUtils.randomNumeric(7);
        grade2.setMatrNumber(matr2);
        grade2.setValue("y");
        gradeRepository.save(grade2);

        List<Performance> performances = calculationService.calculatePerformances(Arrays.asList(matr1, matr2), lecture, performanceScheme);

        Performance performance1 = performances.stream().filter(p -> p.getMatriculationNumber().equals(matr1)).findFirst().get();
        assertEquals("a", performance1.getGrade());
        
        Performance performance2 = performances.stream().filter(p -> p.getMatriculationNumber().equals(matr2)).findFirst().get();
        assertEquals("b", performance2.getGrade());
    }
}