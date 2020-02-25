package de.bp2019.pusl.ui.exerciseScheme;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.BaseUITest;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;
import de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView;
import de.bp2019.pusl.ui.views.user.EditUserView;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * UI test for {@link de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView}
 *
 * @author Luca Dinies
 */

public class ManageExerciseSchemeViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    UserRepository userRepository;

    private ExerciseScheme exerciseScheme;

    private Institute institute;

    public String addExerciseScheme() {

        institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);
        Set<Institute> instituteSet = new HashSet<>();
        instituteSet.add(instituteRepository.findAll().get(0));

        Set<Token> tokenSet = new HashSet<>();
        tokenSet.add(new Token(RandomStringUtils.random(8, true, true), false));

        Set<User> userSet = new HashSet<>();
        userSet.add(userRepository.findByEmailAddress(testProperties.getAdminUsername()));

        exerciseScheme = new ExerciseScheme(RandomStringUtils.random(8, true, false), false, false,
                RandomStringUtils.random(8, true, true), tokenSet, instituteSet, userSet );

        exerciseSchemeRepository.save(exerciseScheme);

        return exerciseScheme.getName();
    }

    @Test
    public void testNewExerciseSchemeButton() throws Exception {
        LOGGER.info("Testing new ExerciseScheme button");
        login(UserType.SUPERADMIN);

        goToURL(ManageExerciseSchemesView.ROUTE);

        findButtonContainingText("Neues Übungsschema").click();

        waitForURL(EditExerciseSchemeView.ROUTE + "/new");
    }

    @Test
    public void testNameButton() throws Exception {
        LOGGER.info("Testing ExerciseScheme name button");
        login(UserType.SUPERADMIN);

        String exerciseSchemeName = addExerciseScheme();

        goToURL(ManageExerciseSchemesView.ROUTE);

        ExerciseScheme exerciseScheme = exerciseSchemeRepository.findByName(exerciseSchemeName);
        String id = exerciseScheme.getId();

        findButtonContainingText(exerciseSchemeName).click();

        waitForURL(EditExerciseSchemeView.ROUTE + "/" + id);

        findElementById("numeric").click();

        findButtonContainingText("Speichern").click();
        waitForURL(ManageExerciseSchemesView.ROUTE);
    }

    @Test
    public void testDeleteButton() throws Exception {
        LOGGER.info("Testing Delete ExerciseScheme Button");
        login(UserType.SUPERADMIN);

        String exerciseSchemeName = addExerciseScheme();

        goToURL(ManageExerciseSchemesView.ROUTE);

        ExerciseScheme exerciseScheme = exerciseSchemeRepository.findByName(exerciseSchemeName);
        String id = exerciseScheme.getId();

        /* click delete button */
        findElementById("delete-" + id).click();
        /* confirm delete button */
        findButtonContainingText("Löschen").click();

        waitUntilDialogVisible("gelöscht");

        assertTrue(exerciseSchemeRepository.findById(id).isEmpty());
    }

}