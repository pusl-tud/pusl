package de.bp2019.pusl.ui;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Test
    public void testLoginCorrectCredentials() throws Exception {
        LOGGER.info("Testing login with correct Credetials");

        waitForLoginRedirect();

        findElementByName("username").sendKeys(testProperties.getSuperadminUsername());
        findElementByName("password").sendKeys(testProperties.getSuperadminPassword());

        findButtonContainingText("Log in").click();

        waitForURL("");
    }

    @Test
    public void testLoginWrongCredentials() throws Exception {
        LOGGER.info("Testing login with wrong Credetials");

        waitForLoginRedirect();

        findElementByName("username").sendKeys(testProperties.getSuperadminUsername());
        findElementByName("password").sendKeys("wrong-password");

        findButtonContainingText("Log in").click();

        timeoutWrongURL("");
    }
}