package de.bp2019.pusl.ui.exerciseScheme;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.BaseUITest;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;
import de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView;

public class EditExerciseSchemeViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Test
    public void testNoInstitutes() throws Exception {
        LOGGER.info("Testing create new ExerciseScheme");

        Institute institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        login(UserType.SUPERADMIN);
        goToURL(EditExerciseSchemeView.ROUTE + "/new");

        ExerciseScheme exerciseScheme = new ExerciseScheme();
        exerciseScheme.setName(RandomStringUtils.random(8, true, true));
        exerciseScheme.setDefaultValue(RandomStringUtils.random(8, true, true));
        LOGGER.info("creating exerciseScheme: " + exerciseScheme.toString());

        findElementById("name").sendKeys(exerciseScheme.getName());
        findElementById("default-Value").sendKeys(exerciseScheme.getDefaultValue());
        findElementById("numeric").click();
        findElementById("flex-Handin").click();
        assertFalse(findElementById("token").isDisplayed());

        findButtonContainingText("Speichern").click();
        timeoutWrongURL(ManageExerciseSchemesView.ROUTE);


    }

    @Test
    public void testCreateNewExerciseScheme() throws Exception {
        LOGGER.info("Testing create new ExerciseScheme");

        Institute institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        login(UserType.SUPERADMIN);
        goToURL(EditExerciseSchemeView.ROUTE + "/new");

        ExerciseScheme exerciseScheme = new ExerciseScheme();
        exerciseScheme.setName(RandomStringUtils.random(8, true, true));
        exerciseScheme.setDefaultValue(RandomStringUtils.random(8, true, true));
        LOGGER.info("creating exerciseScheme: " + exerciseScheme.toString());

        findElementById("name").sendKeys(exerciseScheme.getName());
        findMSCBByIdAndSelectByTexts("institutes", Arrays.asList(institute.getName()));
        findElementById("default-Value").sendKeys(exerciseScheme.getDefaultValue());
        findElementById("flex-Handin").click();
        assertTrue(findElementById("token").isDisplayed());

        findElementById("save").click();
        waitForURL(ManageExerciseSchemesView.ROUTE);


    }

}