package de.bp2019.pusl.ui;

import de.bp2019.pusl.ui.views.user.ManageUsersView;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.ui.views.AccountView;

/**
 * UI test for {@link AccountView}
 * 
 * @author Leon Chemnitz
 */
public class AccountViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountViewIT.class);

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccess() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(AccountView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(AccountView.ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURL(AccountView.ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURL(AccountView.ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testNoName() throws Exception {
        LOGGER.info("Testing access");

        login(UserType.SUPERADMIN);
        goToURL(AccountView.ROUTE);

        findButtonContainingText("Änderungen speichern").click();
        timeoutWrongURL(ManageUsersView.ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testNameEmail() throws Exception {
        LOGGER.info("Testing access");

        login(UserType.SUPERADMIN);
        goToURL(AccountView.ROUTE);

        findElementById("firstName").sendKeys(RandomStringUtils.random(8, true, false));
        findElementById("lastName").sendKeys(RandomStringUtils.random(8, true, false));
        findElementById("email").sendKeys(
                RandomStringUtils.random(8, true, false) + "@" +
                        RandomStringUtils.random(8, true, false) + ".de");

        findButtonContainingText("Änderungen speichern").click();

        waitForURL(ManageUsersView.ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testPasswordToShort() throws Exception {
        LOGGER.info("Testing access");

        login(UserType.SUPERADMIN);
        goToURL(AccountView.ROUTE);

        findElementById("firstName").sendKeys(RandomStringUtils.random(8, true, false));
        findElementById("lastName").sendKeys(RandomStringUtils.random(8, true, false));
        findElementById("email").sendKeys(
                RandomStringUtils.random(8, true, false) + "@" +
                        RandomStringUtils.random(8, true, false) + ".de");

        String random = RandomStringUtils.random(6, true, true);

        findPasswordFieldById("password").sendKeys(random);
        findPasswordFieldById("confirmPassword").sendKeys(random);

        findButtonContainingText("Änderungen speichern").click();

        timeoutWrongURL(ManageUsersView.ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testPasswordNotEqual() throws Exception {
        LOGGER.info("Testing access");

        login(UserType.SUPERADMIN);
        goToURL(AccountView.ROUTE);

        findElementById("firstName").sendKeys(RandomStringUtils.random(8, true, false));
        findElementById("lastName").sendKeys(RandomStringUtils.random(8, true, false));
        findElementById("email").sendKeys(
                RandomStringUtils.random(8, true, false) + "@" +
                        RandomStringUtils.random(8, true, false) + ".de");

        findPasswordFieldById("password").sendKeys(RandomStringUtils.random(8, true, true));
        findPasswordFieldById("confirmPassword").sendKeys(RandomStringUtils.random(8, true, true));

        findButtonContainingText("Änderungen speichern").click();

        timeoutWrongURL(ManageUsersView.ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testNewPassword() throws Exception {
        LOGGER.info("Testing access");

        login(UserType.SUPERADMIN);
        goToURL(AccountView.ROUTE);

        findElementById("firstName").sendKeys(RandomStringUtils.random(8, true, false));
        findElementById("lastName").sendKeys(RandomStringUtils.random(8, true, false));
        findElementById("email").sendKeys(
                RandomStringUtils.random(8, true, false) + "@" +
                        RandomStringUtils.random(8, true, false) + ".de");

        String random = RandomStringUtils.random(8, true, true);

        findPasswordFieldById("password").sendKeys(random);
        findPasswordFieldById("confirmPassword").sendKeys(random);

        findButtonContainingText("Änderungen speichern").click();

        waitForURL(ManageUsersView.ROUTE);
    }


}