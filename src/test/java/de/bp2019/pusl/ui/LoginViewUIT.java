package de.bp2019.pusl.ui;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUIT;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.util.Utils;

/**
 * UI test for {@link LoginView}
 * 
 * @author Leon Chemnitz
 */
public class LoginViewUIT extends BaseUIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewUIT.class);

    @Test
    public void testLoginWrongCredentials() throws Exception {
        LOGGER.info("Testing login with wrong Credetials");

        String password = RandomStringUtils.randomAlphanumeric(14);
        UserType userType = Utils.randomValueFromEnum(UserType.class);
        User user = testUtils.createUser(userType, password);

        waitForLoginRedirect();

        findElementByName("username").sendKeys(user.getEmailAddress());
        findElementByName("password").sendKeys("wrong-password");

        findButtonContainingText("Log in").click();
        timeoutWrongURL(PuslProperties.ROOT_ROUTE);
    }
}