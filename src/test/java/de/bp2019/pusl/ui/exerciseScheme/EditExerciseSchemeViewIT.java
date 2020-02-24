package de.bp2019.pusl.ui.exerciseScheme;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.BaseUITest;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;
import de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView;
import de.bp2019.pusl.ui.views.user.EditUserView;
import de.bp2019.pusl.ui.views.user.ManageUsersView;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class EditExerciseSchemeViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstituteRepository instituteRepository;

    private Institute institute;

    private Random random;
    @Test
    public void testNoInstitutes() throws Exception {
        LOGGER.info("Testing create new ExerciseScheme");

        institute = new Institute(RandomStringUtils.random(8, true, true));
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

        findButtonContainingText("Speichern");
        timeoutWrongURL(ManageExerciseSchemesView.ROUTE);


    }
/*
    ExerciseScheme savedExerciseScheme = exerciseSchemeRepository.findByName(exerciseScheme.getName());
    assertTrue(savedExerciseScheme.getIsNumeric());
    assertTrue(savedExerciseScheme.isFlexHandin());
    assertEquals(exerciseScheme.getDefaultValue(), savedExerciseScheme.getDefaultValue());

*/
}