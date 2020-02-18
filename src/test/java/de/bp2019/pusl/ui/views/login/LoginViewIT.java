package de.bp2019.pusl.ui.views.login;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.ui.views.BaseUITest;

public class LoginViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Test
    public void testLoginCorrectCredentials() throws Exception {
        LOGGER.info("Testing login with correct Credetials");

        waitForLoginRedirect();
        LoginViewElement loginView = new LoginViewElement(this);
        
        loginView.login(testProperties.getSuperadminUsername(), testProperties.getSuperadminPassword());

        waitForURL("");
    }

    @Test
    public void testLoginWrongCredentials() throws Exception {
        LOGGER.info("Testing login with wrong Credetials");

        waitForLoginRedirect();
        LoginViewElement loginView = new LoginViewElement(this);
        
        loginView.login(testProperties.getSuperadminUsername(), "wrong-password");

        timeoutWrongURL("");
    }
}