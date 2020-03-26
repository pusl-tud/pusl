package de.bp2019.pusl.ui.user;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.views.user.EditUserView;
import de.bp2019.pusl.ui.views.user.ManageUsersView;

/**
 * UI test for {@link ManageUsersView}
 * 
 * @author Leon Chemnitz
 */
public class ManageUsersViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

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

        User user = testUtils.createUser(UserType.ADMIN);
        String id = user.getId().toString();
        String name = user.getFullName();
        
        goToURL(ManageUsersView.ROUTE);

        /* click name button */
        findButtonContainingText(name).click();

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

        User user = testUtils.createUser(UserType.ADMIN);
        String id = user.getId().toString();
        String email = user.getEmailAddress();

        goToURL(ManageUsersView.ROUTE);

        /* click delete button */
        findElementById("delete-" + id).click();

        acceptConfirmDeletionDialog(email);

        assertTrue(userRepository.findByEmailAddress(user.getEmailAddress()).isEmpty());
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