package de.bp2019.pusl.ui.views.user;

import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.views.BaseUITest;
import de.bp2019.pusl.ui.views.LoginViewIT;

public class ManageUsersViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    UserRepository userRepository;

    @Test
    public void testNewUserButton() throws Exception {
        LOGGER.info("Testing new User button");
        login(UserType.SUPERADMIN);

        goToURL(ManageUsersView.ROUTE);

        findButtonContainingText("Neuer Nutzer").click();

        waitForURL(EditUserView.ROUTE + "/new");
    }

    @Test
    public void testNameButton() throws Exception {
        LOGGER.info("Testing User name button");
        login(UserType.SUPERADMIN);

        goToURL(ManageUsersView.ROUTE);
        
        User user = userRepository.findByEmailAddress(testProperties.getAdminUsername());
        String name = UserService.getFullName(user);
        String id = user.getId().toString();

        findButtonContainingText(name).click();

        waitForURL(EditUserView.ROUTE + "/" + id);
    }

    @Test
    public void testDeleteButton() throws Exception {
        LOGGER.info("Testing Delete User Button");
        login(UserType.SUPERADMIN);

        goToURL(ManageUsersView.ROUTE);

        User user = userRepository.findByEmailAddress(testProperties.getAdminUsername());
        String id = user.getId().toString();

        /* click delete button */
        findElementById("delete-" + id).click();
        /* confirm delete button */
        findButtonContainingText("Löschen").click();

        waitUntilDialogVisible("gelöscht");
        
        assertNull(userRepository.findByEmailAddress(testProperties.getAdminUsername()));
    }
}