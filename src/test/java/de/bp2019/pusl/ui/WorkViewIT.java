package de.bp2019.pusl.ui;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.ui.views.WorkView;

/**
 * UI test for {@link WorkView}
 * 
 * @author Leon Chemnitz
 */
public class WorkViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkViewIT.class);

    @Test
    public void testSaveGradeNumeric() throws Exception {
        LOGGER.info("Testing save grade");

        String matrNumber = "2920560";

        for (UserType userType : UserType.values()) {
            String value = RandomStringUtils.randomNumeric(1);

            Institute institute = new Institute();
            institute.setName(RandomStringUtils.randomAlphanumeric(16));
            instituteRepository.save(institute);

            ExerciseScheme exerciseScheme = new ExerciseScheme();
            exerciseScheme.setName(RandomStringUtils.randomAlphanumeric(16));
            exerciseScheme.setInstitutes(Sets.newSet(institute));
            exerciseScheme.setIsNumeric(true);
            exerciseSchemeRepository.save(exerciseScheme);

            Exercise exercise = new Exercise();
            exercise.setName(RandomStringUtils.randomAlphanumeric(16));
            exercise.setScheme(exerciseScheme);
            exercise.setAssignableByHIWI(true);

            User user = testUtils.createUser(userType);
            user.setInstitutes(Sets.newSet(institute));
            userRepository.save(user);

            Lecture lecture = new Lecture();
            lecture.setName(RandomStringUtils.randomAlphanumeric(16));
            lecture.setHasAccess(Sets.newSet(user.getId()));
            lecture.setInstitutes(Sets.newSet(institute));
            lecture.setExercises(Arrays.asList(exercise));
            lectureRepository.save(lecture);

            LOGGER.info("matrNumber should be: " + matrNumber);
            LOGGER.info("lecture should be: " + lecture);
            LOGGER.info("exercise should be: " + exercise);
            LOGGER.info("value should be: " + value);
            LOGGER.info("gradedBy should be: " + user);

            login(user);
            findElementById("work-view-gc-matrNumber").sendKeys(matrNumber);

            WebElement lectureComboBox = findElementById("work-view-gc-lecture");
            lectureComboBox.sendKeys(lecture.getName());
            Thread.sleep(2000);
            lectureComboBox.sendKeys(Keys.RETURN);
            Thread.sleep(500);

            WebElement exerciseComboBox = findElementById("work-view-gc-exercise");
            exerciseComboBox.sendKeys(exercise.getName());
            Thread.sleep(500);
            exerciseComboBox.sendKeys(Keys.RETURN);
            Thread.sleep(500);

            findElementById("work-view-gc-numeric").sendKeys(value);

            findButtonContainingText("Note eintragen").click();

            Thread.sleep(500);

            Grade grade = gradeRepository.findAll().get(0);

            LOGGER.info("grade saved was: " + grade);

            assertEquals(matrNumber, grade.getMatrNumber());
            assertEquals(lecture, grade.getLecture());
            assertEquals(exercise, grade.getExercise());
            assertEquals(Double.valueOf(value), Double.valueOf(grade.getValue()));
            assertEquals(user, grade.getGradedBy());

            gradeRepository.deleteAll();
            lectureRepository.deleteAll();
            instituteRepository.deleteAll();
            exerciseSchemeRepository.deleteAll();

            logout();
        }
    }

    @Test
    public void testSaveGradeToken() throws Exception {
        LOGGER.info("Testing save grade token");

        String matrNumber = "2920560";

        for (UserType userType : UserType.values()) {
            Institute institute = new Institute();
            institute.setName(RandomStringUtils.randomAlphanumeric(16));
            instituteRepository.save(institute);

            Token token1 = new Token();
            token1.setAssignableByHIWI(true);
            token1.setName(RandomStringUtils.randomAlphanumeric(16));

            Token token2 = new Token();
            token2.setAssignableByHIWI(true);
            token2.setName(RandomStringUtils.randomAlphanumeric(16));

            ExerciseScheme exerciseScheme = new ExerciseScheme();
            exerciseScheme.setName(RandomStringUtils.randomAlphanumeric(16));
            exerciseScheme.setInstitutes(Sets.newSet(institute));
            exerciseScheme.setIsNumeric(false);
            exerciseScheme.setDefaultValueToken(token1);
            exerciseScheme.setTokens(Sets.newSet(token1, token2));
            exerciseSchemeRepository.save(exerciseScheme);

            Exercise exercise = new Exercise();
            exercise.setName(RandomStringUtils.randomAlphanumeric(16));
            exercise.setScheme(exerciseScheme);
            exercise.setAssignableByHIWI(true);

            User user = testUtils.createUser(userType);
            user.setInstitutes(Sets.newSet(institute));
            userRepository.save(user);

            Lecture lecture = new Lecture();
            lecture.setName(RandomStringUtils.randomAlphanumeric(16));
            lecture.setHasAccess(Sets.newSet(user.getId()));
            lecture.setInstitutes(Sets.newSet(institute));
            lecture.setExercises(Arrays.asList(exercise));
            lectureRepository.save(lecture);

            LOGGER.info("matrNumber should be: " + matrNumber);
            LOGGER.info("lecture should be: " + lecture);
            LOGGER.info("exercise should be: " + exercise);
            LOGGER.info("token should be: " + token2);
            LOGGER.info("gradedBy should be: " + user);

            login(user);
            findElementById("work-view-gc-matrNumber").sendKeys(matrNumber);

            WebElement lectureComboBox = findElementById("work-view-gc-lecture");
            lectureComboBox.sendKeys(lecture.getName());
            Thread.sleep(2000);
            lectureComboBox.sendKeys(Keys.RETURN);
            Thread.sleep(500);

            WebElement exerciseComboBox = findElementById("work-view-gc-exercise");
            exerciseComboBox.sendKeys(exercise.getName());
            Thread.sleep(500);
            exerciseComboBox.sendKeys(Keys.RETURN);
            Thread.sleep(500);

            WebElement tokenComboBox = findElementById("work-view-gc-token");
            tokenComboBox.sendKeys(token2.getName());
            Thread.sleep(500);
            tokenComboBox.sendKeys(Keys.RETURN);
            Thread.sleep(500);

            findButtonContainingText("Note eintragen").click();

            Thread.sleep(500);

            Grade grade = gradeRepository.findAll().get(0);

            LOGGER.info("grade saved was: " + grade);

            assertEquals(matrNumber, grade.getMatrNumber());
            assertEquals(lecture, grade.getLecture());
            assertEquals(exercise, grade.getExercise());
            assertEquals(token2.getName(), grade.getValue());
            assertEquals(user, grade.getGradedBy());

            gradeRepository.deleteAll();
            lectureRepository.deleteAll();
            instituteRepository.deleteAll();
            exerciseSchemeRepository.deleteAll();

            logout();
        }
    }

    @Test
    public void testEditGradePopup() throws Exception {
        LOGGER.info("Testing edit grade popup");

        String matrNumber = "2920560";

        for (UserType userType : UserType.values()) {
            Institute institute = new Institute();
            institute.setName(RandomStringUtils.randomAlphanumeric(16));
            instituteRepository.save(institute);

            ExerciseScheme exerciseScheme = new ExerciseScheme();
            exerciseScheme.setName(RandomStringUtils.randomAlphanumeric(16));
            exerciseScheme.setInstitutes(Sets.newSet(institute));
            exerciseScheme.setIsNumeric(true);
            exerciseSchemeRepository.save(exerciseScheme);

            String value = RandomStringUtils.randomNumeric(1);

            Exercise exercise = new Exercise();
            exercise.setName(RandomStringUtils.randomAlphanumeric(16));
            exercise.setScheme(exerciseScheme);
            exercise.setAssignableByHIWI(true);

            User user = testUtils.createUser(userType);
            user.setInstitutes(Sets.newSet(institute));
            userRepository.save(user);

            Lecture lecture = new Lecture();
            lecture.setName(RandomStringUtils.randomAlphanumeric(16));
            lecture.setHasAccess(Sets.newSet(user.getId()));
            lecture.setInstitutes(Sets.newSet(institute));
            lecture.setExercises(Arrays.asList(exercise));
            lectureRepository.save(lecture);

            Grade grade = new Grade();
            grade.setMatrNumber(matrNumber);
            grade.setLecture(lecture);
            grade.setExercise(exercise);
            grade.setValue(value);
            grade.setGradedBy(user);
            grade.setHandIn(LocalDate.now());
            gradeRepository.save(grade);

            login(user);

            findElementById("vtabs-einsehen").click();
            Thread.sleep(500);

            Actions actions = new Actions(driver);
            WebElement gridItem = driver.findElement(By.tagName("vaadin-grid-cell-content"));
            actions.doubleClick(gridItem).perform();
            
            Thread.sleep(500);

            String newValue = RandomStringUtils.randomNumeric(2);

            findElementById("dialog-gc-numeric").sendKeys(Keys.BACK_SPACE);
            findElementById("dialog-gc-numeric").sendKeys(newValue);

            findButtonContainingText("speichern").click();
            
            Thread.sleep(1000);

            Grade foundGrade = gradeRepository.findById(grade.getId().toString()).get();

            LOGGER.info("grade saved was: " + grade);

            assertEquals(Double.valueOf(newValue), Double.valueOf(foundGrade.getValue()));

            gradeRepository.deleteAll();
            lectureRepository.deleteAll();
            instituteRepository.deleteAll();
            exerciseSchemeRepository.deleteAll();

            logout();
        }
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccess() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(WorkView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(WorkView.ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURL(WorkView.ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURL(WorkView.ROUTE);
    }
}