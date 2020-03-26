package de.bp2019.pusl.ui.exercisescheme;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.dialogs.ConfirmDeletionDialog;
import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;
import de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView;

/**
 * UI test for
 * {@link de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView}
 *
 * @author Luca Dinies
 */
public class ManageExerciseSchemesViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    UserRepository userRepository;

    private ExerciseScheme exerciseScheme;

    private Institute institute;

    /**
     * @author Luca Dinies
     */
    private String addExerciseScheme() {
        institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);
        Set<Institute> instituteSet = new HashSet<>();
        instituteSet.add(instituteRepository.findAll().get(0));

        Set<Token> tokenSet = new HashSet<>();
        tokenSet.add(new Token(RandomStringUtils.random(8, true, true), false));

        Set<User> userSet = new HashSet<>();
        userSet.add(userRepository.findByEmailAddress(testProperties.getAdminUsername()).get());

        exerciseScheme = new ExerciseScheme(RandomStringUtils.random(8, true, false), false, false,
                RandomStringUtils.random(8, true, true), tokenSet, instituteSet, userSet);

        exerciseSchemeRepository.save(exerciseScheme);

        return exerciseScheme.getName();
    }

    /**
     * @author Luca Dinies
     * @throws Exception
     */
    @Test
    public void testNewExerciseSchemeButton() throws Exception {
        LOGGER.info("Testing new ExerciseScheme button");
        login(UserType.SUPERADMIN);

        goToURL(ManageExerciseSchemesView.ROUTE);

        findButtonContainingText("Neues Übungsschema").click();

        waitForURL(EditExerciseSchemeView.ROUTE + "/new");
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

        goToURL(ManageExerciseSchemesView.ROUTE);

        ExerciseScheme exerciseScheme = exerciseSchemeRepository.findByName(exerciseSchemeName).get();
        ObjectId id = exerciseScheme.getId();

        findButtonContainingText(exerciseSchemeName).click();

        waitForURL(EditExerciseSchemeView.ROUTE + "/" + id.toString());

        findElementById("numeric").click();

        findButtonContainingText("Speichern").click();
        waitForURL(ManageExerciseSchemesView.ROUTE);
    }

    /**
     * @author Luca Dinies
     * @throws Exception
     */
    @Test
    public void testDeleteButton() throws Exception {
        LOGGER.info("Testing Delete ExerciseScheme Button");
        login(UserType.SUPERADMIN);

        String exerciseSchemeName = addExerciseScheme();

        goToURL(ManageExerciseSchemesView.ROUTE);

        ExerciseScheme exerciseScheme = exerciseSchemeRepository.findByName(exerciseSchemeName).get();
        ObjectId id = exerciseScheme.getId();

        /* click delete button */
        findElementById("delete-" + id.toString()).click();
        /* confirm delete button */
        findButtonContainingText("Löschen").click();

        findElementById(ConfirmDeletionDialog.ID);

        // TODO: jajaja

        //assertTrue(exerciseSchemeRepository.findById(id.toString()).isEmpty());
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
}