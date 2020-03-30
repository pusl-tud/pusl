package de.bp2019.pusl.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.BaseUIT;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.views.AccountView;
import de.bp2019.pusl.ui.views.user.ManageUsersView;

/**
 * UI test for {@link AccountView}
 * 
 * @author Leon Chemnitz
 */
public class AccountViewUIT extends BaseUIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountViewUIT.class);

    @Autowired
    UserRepository userRepository;

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
     * @author Luca Dinies, Leon Chemnitz
     */
    @Test
    public void testSaveSuccess() throws Exception {
        LOGGER.info("Testing save success");

        for(UserType type: UserType.values()){
            User oldUser = login(type);
            ObjectId id = oldUser.getId();
            String password = oldUser.getPassword();
            String email = oldUser.getEmailAddress();

            goToURL(AccountView.ROUTE);
        
            String firstName = RandomStringUtils.randomAlphanumeric(14);
            LOGGER.info("first name: " + firstName);
            String lastName = RandomStringUtils.randomAlphanumeric(14);
            LOGGER.info("last name: " + lastName);
    
            clearFieldById("firstName");
            findElementById("firstName").sendKeys(firstName);
            clearFieldById("lastName");
            findElementById("lastName").sendKeys(lastName);
    
            findButtonContainingText("Änderungen speichern").click();
    
            waitForURL(PuslProperties.ROOT_ROUTE);
    
            User foundUser = userRepository.findById(id.toString()).get();
            LOGGER.info("New User: " + foundUser.toString());
    
            assertEquals(firstName, foundUser.getFirstName());
            assertEquals(lastName, foundUser.getLastName());
            assertEquals(email, foundUser.getEmailAddress());
            assertEquals(password, foundUser.getPassword());

            logout();
        }
    }

    /**
     * @throws Exception
     * @author Luca Dinies, Leon Chemnitz
     */
    @Test
    public void testPasswordToShort() throws Exception {
        LOGGER.info("Testing access");

        login(UserType.SUPERADMIN);
        goToURL(AccountView.ROUTE);

        String password = RandomStringUtils.random(6, true, true);

        findPasswordFieldById("password").sendKeys(password);
        findPasswordFieldById("confirmPassword").sendKeys(password);

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
                RandomStringUtils.random(8, true, false) + "@" + RandomStringUtils.random(8, true, false) + ".de");

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

        User user = login(UserType.SUPERADMIN);
        goToURL(AccountView.ROUTE);

        String password = RandomStringUtils.random(8, true, true);

        findPasswordFieldById("password").sendKeys(password);
        findPasswordFieldById("confirmPassword").sendKeys(password);

        findButtonContainingText("Änderungen speichern").click();

        waitForURL(PuslProperties.ROOT_ROUTE);

        String encodedPassword = userRepository.findById(user.getId().toString()).get().getPassword();

        assertTrue(passwordEncoder.matches(password, encodedPassword));
    }

}