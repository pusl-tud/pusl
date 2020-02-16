package de.bp2019.pusl.ui.views.user.manageUsers;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.ui.views.BaseUITest;
import de.bp2019.pusl.ui.views.login.LoginViewElement;
import de.bp2019.pusl.ui.views.user.EditUserView;
import de.bp2019.pusl.ui.views.user.ManageUsersView;

public class ManageUsersViewIT extends BaseUITest {

    @Test
    public void newUserButton() throws Exception{
        waitForLoginRedirect();
        LoginViewElement loginView = new LoginViewElement(driver);
        loginView.loginAdminCredentials();

        goToURL(ManageUsersView.ROUTE);

        ManageUsersViewElement manageUsersView = new ManageUsersViewElement(driver);

        manageUsersView.clickNewUserButton();

        waitForURL(EditUserView.ROUTE + "/new");
    }
}