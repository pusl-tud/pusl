package de.bp2019.pusl.ui.views.login;

import org.junit.Test;

import de.bp2019.pusl.ui.views.BaseUITest;

public class LoginViewIT extends BaseUITest {

    @Test
    public void LoginWrongCredentials() throws Exception {
        waitForLoginRedirect();
        LoginViewElement loginView = new LoginViewElement(driver);
        
        loginView.login("wrong", "password");

        timeoutWrongURL("");
    }

    @Test
    public void LoginAdminCredentials() throws Exception {
        waitForLoginRedirect();
        LoginViewElement loginView = new LoginViewElement(driver);
        
        loginView.loginAdminCredentials();

        waitForURL("");
    }


}