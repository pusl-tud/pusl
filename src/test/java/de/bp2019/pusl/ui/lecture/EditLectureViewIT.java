package de.bp2019.pusl.ui.lecture;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.ui.views.lecture.EditLectureView;
import de.bp2019.pusl.ui.views.lecture.ManageLecturesView;

/**
 * UI test for {@link EditLectureView}
 * 
 * @author Leon Chemnitz
 */
public class EditLectureViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditLectureViewIT.class);

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testCreateNewLecture() throws Exception {
        LOGGER.info("testing create new Lecture");

        Institute institute = new Institute();
        institute.setName(RandomStringUtils.randomAlphabetic(16));
        instituteRepository.save(institute);

        ExerciseScheme exerciseScheme = new ExerciseScheme();
        exerciseScheme.setName(RandomStringUtils.randomAlphanumeric(16));
        exerciseScheme.setInstitutes(Sets.newSet(institute));
        exerciseSchemeRepository.save(exerciseScheme);

        login(UserType.SUPERADMIN);

        goToURL(EditLectureView.ROUTE + "/new");

        String name = RandomStringUtils.randomAlphanumeric(16);
        findElementById("lecture-name").sendKeys(name);
        findMSCBByIdAndSelectByTexts("lecture-institutes", Arrays.asList(institute.getName()));

        String exerciseName = RandomStringUtils.randomAlphanumeric(16);
        findElementById("new-exercise-name").sendKeys(exerciseName);

        findElementById("new-exercise-scheme").sendKeys(exerciseScheme.getName());
        Thread.sleep(500);
        findButtonContainingText("hinzufügen").click();

        findElementById("vtabs-leistungen").click();
        String performanceName = RandomStringUtils.randomAlphabetic(16);
        findElementById("performance-name").sendKeys(performanceName);
        findElementById("create-performance").click();

        findButtonContainingText("Speichern").click();
        
        waitForURL(ManageLecturesView.ROUTE);

        Lecture lecture = lectureRepository.findAll().get(0);
        
        assertEquals(name, lecture.getName());
        assertEquals(exerciseName, lecture.getExercises().get(0).getName());
        assertEquals(exerciseScheme, lecture.getExercises().get(0).getScheme());
        assertEquals(performanceName, lecture.getPerformanceSchemes().get(1).getName());
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
        goToURL(EditLectureView.ROUTE + "/new");
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(EditLectureView.ROUTE + "/new");
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(EditLectureView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(EditLectureView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
    }
}