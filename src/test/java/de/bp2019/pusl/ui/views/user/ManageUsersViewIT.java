package de.bp2019.pusl.ui.views.user;

import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.views.BaseUITest;
import de.bp2019.pusl.ui.views.login.LoginViewIT;

public class ManageUsersViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    UserRepository userRepository;

    @Test
    public void testNewUserButton() throws Exception {
        LOGGER.info("Testing new User button");
        login(UserType.SUPERADMIN);

        goToURL(ManageUsersView.ROUTE);

        ManageUsersViewElement manageUsersView = new ManageUsersViewElement(this);

        manageUsersView.clickNewUserButton();

        waitForURL(EditUserView.ROUTE + "/new");
    }

    @Test
    public void testNameButton() throws Exception {
        LOGGER.info("Testing User name button");
        login(UserType.SUPERADMIN);

        goToURL(ManageUsersView.ROUTE);

        ManageUsersViewElement manageUsersView = new ManageUsersViewElement(this);

        manageUsersView.clickAdminUserNameButton();

        User adminUser = userRepository.findByEmailAddress(testProperties.getAdminUsername());
        String userID = adminUser.getId().toString();

        waitForURL(EditUserView.ROUTE + "/" + userID);
    }

    @Test
    public void testDeleteButton() throws Exception {
        LOGGER.info("Testing Delete User Button");
        login(UserType.SUPERADMIN);

        goToURL(ManageUsersView.ROUTE);

        ManageUsersViewElement manageUsersView = new ManageUsersViewElement(this);

        User adminUser = userRepository.findByEmailAddress(testProperties.getAdminUsername());
        String userID = adminUser.getId().toString();

        manageUsersView.clickDeleteButton(userID);
        manageUsersView.clickConfirmDeleteButton();
        
    }
}