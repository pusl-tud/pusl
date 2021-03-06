package de.bp2019.pusl.ui;

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
 * UI test for {@link EditInstituteView} and {@link ManageInstitutesView}
 *
 * @author Leon Chemnitz
 */
public class InstituteUIT extends BaseUIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstituteUIT.class);

    @Autowired
    InstituteRepository instituteRepository;

    Institute institute;

    private void goToManageInstitutesView() throws InterruptedException {
        findElementById("institute-menu-button").click();
        waitForURL(ManageInstitutesView.ROUTE);
    }

    private void goToNewInstitute() throws InterruptedException {
        goToManageInstitutesView();
        findElementById("new-institute-button").click();

        waitForURL(EditInstituteView.ROUTE + "/new");
    }

    /**
     * @author Luca Dinies
     */
    private String addInstitute() {
        institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        return institute.getName();
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

        goToManageInstitutesView();

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

        goToManageInstitutesView();

        Institute institute = instituteRepository.findByName(instituteName).get();
        ObjectId id = institute.getId();

        /* click delete button */
        findElementById("delete-" + id.toString()).click();

        findElementById(ConfirmDeletionDialog.ID);

        acceptConfirmDeletionDialog(instituteName);

        assertTrue(instituteRepository.findById(id).isEmpty());
    }

    /**
     * @throws Exception
     * @author Luca Dinies, Leon Chemnitz
     */
    @Test
    public void testCreateNewInstitute() throws Exception {
        LOGGER.info("Testing create new Institute");

        login(UserType.SUPERADMIN);
        goToNewInstitute();

        String name = RandomStringUtils.random(8, true, true);
        findElementById("name").sendKeys(name);
        findButtonContainingText("Speichern").click();

        waitForURL(ManageInstitutesView.ROUTE);
        assertTrue(instituteRepository.findByName(name).isPresent());
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testNoName() throws Exception {
        LOGGER.info("Testing create new Institute");

        login(UserType.SUPERADMIN);
        goToNewInstitute();

        findButtonContainingText("Speichern").click();

        timeoutWrongURL(ManageInstitutesView.ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testDuplicate() throws Exception {
        LOGGER.info("Testing create new Institute");

        Institute institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        login(UserType.SUPERADMIN);
        goToNewInstitute();

        findElementById("name").sendKeys(institute.getName());
        findButtonContainingText("Speichern").click();

        timeoutWrongURL(ManageInstitutesView.ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testAutorisationWithParameters() throws Exception {
        LOGGER.info("Testing autorisation for query Parameters");

        Institute institute = new Institute();
        institute.setName(RandomStringUtils.random(12));

        instituteRepository.save(institute);

        ObjectId id = instituteRepository.findAll().get(0).getId();

        login(UserType.HIWI);

        goToURLandWaitForRedirect(EditInstituteView.ROUTE + "/" + id.toString(), PuslProperties.ROOT_ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testWrongParameters() throws Exception {
        LOGGER.info("Testing wrong query Parameters");

        login(UserType.SUPERADMIN);

        goToURLandWaitForRedirect(EditInstituteView.ROUTE + "/" + RandomStringUtils.random(10),
                PuslProperties.ROOT_ROUTE);
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccessManageInstitutesView() throws Exception {
        LOGGER.info("Testing access ManageInstitutesView");

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
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccessEditInstituteView() throws Exception {
        LOGGER.info("Testing access EditInstituteView");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(EditInstituteView.ROUTE + "/new");
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURLandWaitForRedirect(EditInstituteView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(EditInstituteView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(EditInstituteView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
    }

}