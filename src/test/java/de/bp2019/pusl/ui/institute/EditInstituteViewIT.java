package de.bp2019.pusl.ui.institute;

import static org.junit.Assert.assertTrue;

import de.bp2019.pusl.model.User;
import de.bp2019.pusl.ui.views.user.EditUserView;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.ui.views.institute.EditInstituteView;
import de.bp2019.pusl.ui.views.institute.ManageInstitutesView;

/**
 * UI test for {@link EditInstituteView}
 *
 * @author Leon Chemnitz
 */
public class EditInstituteViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditInstituteViewIT.class);

    @Autowired
    InstituteRepository instituteRepository;


    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccess() throws Exception {
        LOGGER.info("Testing access");

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

    /**
     * @throws Exception
     * @author Luca Dinies, Leon Chemnitz
     */
    @Test
    public void testCreateNewInstitute() throws Exception {
        LOGGER.info("Testing create new Institute");

        login(UserType.SUPERADMIN);
        goToURL(EditInstituteView.ROUTE + "/new");

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
        goToURL(EditInstituteView.ROUTE + "/new");

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
        goToURL(EditInstituteView.ROUTE + "/new");

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
}