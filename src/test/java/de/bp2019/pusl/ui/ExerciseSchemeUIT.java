package de.bp2019.pusl.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.BaseUIT;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;
import de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView;

/**
 * UI test for {@link ManageExerciseSchemesView}
 *
 * @author Leon Chemnitz
 */
public class ExerciseSchemeUIT extends BaseUIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseSchemeUIT.class);

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    UserRepository userRepository;

    private ExerciseScheme exerciseScheme;

    private void goToManageExerciseSchemesView() throws InterruptedException {
        findElementById("leistungsschemas").click();
        waitForURL(ManageExerciseSchemesView.ROUTE);
    }

    private void goToNewExerciseScheme() throws InterruptedException {
        goToManageExerciseSchemesView();
        findElementById("new-exercisescheme-button").click();
        
        waitForURL(EditExerciseSchemeView.ROUTE + "/new");
    }

    /**
     * @author Luca Dinies, Leon Chemnitz
     */
    private String addExerciseScheme() {
        exerciseScheme = new ExerciseScheme();
        exerciseScheme.setName(RandomStringUtils.randomAlphanumeric(14));

        exerciseSchemeRepository.save(exerciseScheme);

        return exerciseScheme.getName();
    }

    /**
     * @author Luca Dinies
     * @throws Exception
     */
    @Test
    public void testNameButton() throws Exception {
        LOGGER.info("Testing ExerciseScheme name button");
        login(UserType.SUPERADMIN);

        String exerciseSchemeName = addExerciseScheme();

        goToManageExerciseSchemesView();

        ExerciseScheme exerciseScheme = exerciseSchemeRepository.findByName(exerciseSchemeName).get();
        ObjectId id = exerciseScheme.getId();

        findButtonContainingText(exerciseSchemeName).click();

        waitForURL(EditExerciseSchemeView.ROUTE + "/" + id.toString());
    }

    /**
     * @author Luca Dinies
     * @throws Exception
     */
    @Test
    public void testDeleteExerciseScheme() throws Exception {
        LOGGER.info("Testing delete exerciseScheme");
        login(UserType.SUPERADMIN);

        String exerciseSchemeName = addExerciseScheme();

        goToManageExerciseSchemesView();

        ExerciseScheme exerciseScheme = exerciseSchemeRepository.findByName(exerciseSchemeName).get();
        ObjectId id = exerciseScheme.getId();

        /* click delete button */
        findElementById("delete-" + id.toString()).click();

        acceptConfirmDeletionDialog(exerciseSchemeName);

        assertTrue(exerciseSchemeRepository.findById(id).isEmpty());
    }

    /**
     * @author Luca Dinies, Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testCreateNewExerciseScheme() throws Exception {
        LOGGER.info("Testing create new ExerciseScheme");

        Institute institute = new Institute(RandomStringUtils.random(16, true, true));
        instituteRepository.save(institute);

        login(UserType.SUPERADMIN);

        goToNewExerciseScheme();

        findElementById("token-based").click();

        String tokenName = RandomStringUtils.randomAlphanumeric(16);
        findElementById("token-name").sendKeys(tokenName);
        findElementById("add-token").click();

        findSelectByIdAndSelectByText("default-value-token", tokenName);

        findMSCBByIdAndSelectByTexts("institutes", Arrays.asList(institute.getName()));

        String name = RandomStringUtils.random(16, true, true);
        findElementById("name").sendKeys(name);

        sendShortcut(Keys.ENTER);

        waitForURL(ManageExerciseSchemesView.ROUTE);

        ExerciseScheme exerciseScheme = exerciseSchemeRepository.findAll().get(0);

        assertEquals(name, exerciseScheme.getName());
        assertEquals(false, exerciseScheme.isNumeric());
        assertEquals(tokenName, exerciseScheme.getDefaultValueToken().getName());
    }

    /**
     * @author Luca Dinies
     * @throws Exception
     */
    @Test
    public void testNoInstitutes() throws Exception {
        LOGGER.info("Testing create new ExerciseScheme");

        login(UserType.SUPERADMIN);

        goToNewExerciseScheme();

        ExerciseScheme exerciseScheme = new ExerciseScheme();
        exerciseScheme.setName(RandomStringUtils.random(8, true, true));
        LOGGER.info("creating exerciseScheme: " + exerciseScheme.toString());

        findElementById("name").sendKeys(exerciseScheme.getName());

        findButtonContainingText("Speichern").click();
        timeoutWrongURL(ManageExerciseSchemesView.ROUTE);
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccessManageExerciseSchemesView() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(ManageExerciseSchemesView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(ManageExerciseSchemesView.ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(ManageExerciseSchemesView.ROUTE, PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(ManageExerciseSchemesView.ROUTE, PuslProperties.ROOT_ROUTE);
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccessEditExerciseSchemeView() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(EditExerciseSchemeView.ROUTE + "/new");
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(EditExerciseSchemeView.ROUTE + "/new");
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(EditExerciseSchemeView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(EditExerciseSchemeView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
    }
}