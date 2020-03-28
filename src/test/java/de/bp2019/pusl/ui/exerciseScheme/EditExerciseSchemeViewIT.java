package de.bp2019.pusl.ui.exercisescheme;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;
import de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView;

/**
 * UI test for {@link EditExerciseSchemeView}
 *
 * @author Luca Dinies
 */
public class EditExerciseSchemeViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);



    /**
     * @author Luca Dinies
     * @throws Exception
     */
    @Test
    public void testNoInstitutes() throws Exception {
        LOGGER.info("Testing create new ExerciseScheme");

        login(UserType.SUPERADMIN);
        goToURL(EditExerciseSchemeView.ROUTE + "/new");

        ExerciseScheme exerciseScheme = new ExerciseScheme();
        exerciseScheme.setName(RandomStringUtils.random(8, true, true));
        LOGGER.info("creating exerciseScheme: " + exerciseScheme.toString());

        findElementById("name").sendKeys(exerciseScheme.getName());

        findButtonContainingText("Speichern").click();
        timeoutWrongURL(ManageExerciseSchemesView.ROUTE);
    }

    /**
     * @author Luca Dinies, Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testCreateNewExerciseScheme() throws Exception {
        LOGGER.info("Testing create new ExerciseScheme");

        Institute institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        login(UserType.SUPERADMIN);
        goToURL(EditExerciseSchemeView.ROUTE + "/new");

        String name = RandomStringUtils.random(16, true, true);
        findElementById("name").sendKeys(name);
        findMSCBByIdAndSelectByTexts("institutes", Arrays.asList(institute.getName()));
        findElementById("token-based").click();

        String tokenName = RandomStringUtils.randomAlphanumeric(16);
        findElementById("token-name").sendKeys(tokenName);
        findElementById("add-token").click();

        Thread.sleep(500);
        findElementById("default-value-token").sendKeys(tokenName);
        findElementById("default-value-token").sendKeys(Keys.ENTER);
        Thread.sleep(500);

        findElementById("save").click();
        
        Thread.sleep(2000);

        ExerciseScheme exerciseScheme = exerciseSchemeRepository.findAll().get(0);

        assertEquals(name, exerciseScheme.getName());
        assertEquals(false, exerciseScheme.isNumeric());
        assertEquals(tokenName, exerciseScheme.getDefaultValueToken().getName());
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