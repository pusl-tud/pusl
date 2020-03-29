package de.bp2019.pusl.ui.institute;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.BaseUIT;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.ui.dialogs.ConfirmDeletionDialog;
import de.bp2019.pusl.ui.views.institute.EditInstituteView;
import de.bp2019.pusl.ui.views.institute.ManageInstitutesView;

/**
 * UI test for {@link ManageInstitutesView}
 *
 * @author Leon Chemnitz
 */
public class ManageInstitutesViewUIT extends BaseUIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageInstitutesViewUIT.class);

    Institute institute;

    @Autowired
    InstituteRepository instituteRepository;

    /**
     * @author Luca Dinies
     */
    private String addInstitute() {
        institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        return institute.getName();
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
        goToURL(ManageInstitutesView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURLandWaitForRedirect(ManageInstitutesView.ROUTE, PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(ManageInstitutesView.ROUTE, PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(ManageInstitutesView.ROUTE, PuslProperties.ROOT_ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testNewExerciseSchemeButton() throws Exception {
        LOGGER.info("Testing new Institute button");
        login(UserType.SUPERADMIN);

        goToURL(ManageInstitutesView.ROUTE);

        findButtonContainingText("Neues Institut").click();

        waitForURL(EditInstituteView.ROUTE + "/new");
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testNameButton() throws Exception {
        LOGGER.info("Testing Institute name button");
        login(UserType.SUPERADMIN);

        String instituteName = addInstitute();

        goToURL(ManageInstitutesView.ROUTE);

        Institute institute = instituteRepository.findByName(instituteName).get();
        ObjectId id = institute.getId();

        findButtonContainingText(instituteName).click();
        waitForURL(EditInstituteView.ROUTE + "/" + id.toString());

        findButtonContainingText("Speichern").click();
        waitForURL(ManageInstitutesView.ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies, Leon Chemnitz
     */
    @Test
    public void testDeleteButton() throws Exception {
        LOGGER.info("Testing Delete Institute Button");
        login(UserType.SUPERADMIN);

        String instituteName = addInstitute();

        goToURL(ManageInstitutesView.ROUTE);

        Institute institute = instituteRepository.findByName(instituteName).get();
        ObjectId id = institute.getId();

        /* click delete button */
        findElementById("delete-" + id.toString()).click();

        findElementById(ConfirmDeletionDialog.ID);

        acceptConfirmDeletionDialog(instituteName);

        assertTrue(instituteRepository.findById(id.toString()).isEmpty());
    }
}