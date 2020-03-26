package de.bp2019.pusl.ui.exercisescheme;

import static org.junit.Assert.assertTrue;

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
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;
import de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView;

/**
 * UI test for
 * {@link ManageExerciseSchemesView}
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

        acceptConfirmDeletionDialog(exerciseSchemeName);

        assertTrue(exerciseSchemeRepository.findById(id.toString()).isEmpty());
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