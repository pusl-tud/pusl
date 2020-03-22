package de.bp2019.pusl.ui.user;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.dialogs.ConfirmDeletionDialog;
import de.bp2019.pusl.ui.views.user.EditUserView;
import de.bp2019.pusl.ui.views.user.ManageUsersView;

/**
 * UI test for {@link ManageUsersView}
 * 
 * @author Leon Chemnitz
 */
public class ManageUsersViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    UserRepository userRepository;

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testNewUserButton() throws Exception {
        LOGGER.info("Testing new User button");
        login(UserType.SUPERADMIN);

        goToURL(ManageUsersView.ROUTE);

        findButtonContainingText("Neuer Nutzer").click();

        waitForURL(EditUserView.ROUTE + "/new");
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testNameButton() throws Exception {
        LOGGER.info("Testing User name button");
        login(UserType.SUPERADMIN);

        goToURL(ManageUsersView.ROUTE);

        User user = userRepository.findByEmailAddress(testProperties.getAdminUsername()).get();
        String id = user.getId().toString();

        /* click delete button */
        findElementById("create-" + id).click();

        waitForURL(EditUserView.ROUTE + "/" + id);
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testDeleteButton() throws Exception {
        LOGGER.info("Testing Delete User Button");
        login(UserType.SUPERADMIN);

        goToURL(ManageUsersView.ROUTE);

        User user = userRepository.findByEmailAddress(testProperties.getAdminUsername()).get();
        String id = user.getId().toString();

        /* click delete button */
        findElementById("delete-" + id).click();
        /* confirm delete button */
        findButtonContainingText("Löschen").click();

        findElementById(ConfirmDeletionDialog.ID);

        // TODO: jajaja
        //assertTrue(userRepository.findByEmailAddress(testProperties.getAdminUsername()).isEmpty());
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
        goToURL(ManageUsersView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(ManageUsersView.ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(ManageUsersView.ROUTE, PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
    }
}