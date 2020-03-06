package de.bp2019.pusl.ui.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.views.LecturesView;
import de.bp2019.pusl.ui.views.user.EditUserView;
import de.bp2019.pusl.ui.views.user.ManageUsersView;

/**
 * UI test for {@link EditUserView}
 * 
 * @author Leon Chemnitz
 */
public class EditUserViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Institute institute;

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testCreateNewUserSuccess() throws Exception {
        LOGGER.info("Testing create new User success");

        institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        login(UserType.SUPERADMIN);
        goToURL(EditUserView.ROUTE + "/new");

        User user = new User();
        user.setEmailAddress(RandomStringUtils.random(8, true, true) + "@" + RandomStringUtils.random(8, true, true)
                + "." + RandomStringUtils.random(4, true, false));
        user.setFirstName(RandomStringUtils.random(8, true, true));
        user.setLastName(RandomStringUtils.random(8, true, true));
        user.setPassword(RandomStringUtils.random(8, true, true));
        user.setType(UserType.SUPERADMIN);

        LOGGER.info("creating user: " + user.toString());

        findElementById("email-address").sendKeys(user.getEmailAddress());
        findElementById("first-name").sendKeys(user.getFirstName());
        findElementById("last-name").sendKeys(user.getLastName());
        findMSCBByIdAndSelectByTexts("institutes", Arrays.asList(institute.getName()));
        findSelectByIdAndSelectByText("user-type", user.getType().toString());
        findPasswordFieldById("password").sendKeys(user.getPassword());
        findPasswordFieldById("confirm-password").sendKeys(user.getPassword());

        findButtonContainingText("Speichern").click();
        waitForURL(ManageUsersView.ROUTE);

        User savedUser = userRepository.findByEmailAddress(user.getEmailAddress()).get();
        assertNotNull(savedUser);
        assertEquals(user.getFirstName(), savedUser.getFirstName());
        assertEquals(user.getLastName(), savedUser.getLastName());
        assertTrue(passwordEncoder.matches(user.getPassword(), savedUser.getPassword()));
        assertEquals(user.getType(), savedUser.getType());
        savedUser.getInstitutes().forEach(i -> assertEquals(institute.getName(), i.getName()));
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testEditUserButNotPassword() throws Exception {
        LOGGER.info("Testing create new User success");

        institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        login(UserType.SUPERADMIN);

        User user = new User();
        user.setEmailAddress(RandomStringUtils.random(8, true, true) + "@" + RandomStringUtils.random(8, true, true)
                + "." + RandomStringUtils.random(4, true, false));
        user.setFirstName(RandomStringUtils.random(8, true, true));
        user.setLastName(RandomStringUtils.random(8, true, true));
        user.setPassword(passwordEncoder.encode(RandomStringUtils.random(8, true, true)));
        user.setInstitutes(new HashSet<Institute>(instituteRepository.findAll()));
        user.setType(UserType.SUPERADMIN);

        userRepository.save(user);

        user = userRepository.findByEmailAddress(user.getEmailAddress()).get();

        goToURL(EditUserView.ROUTE + "/" + user.getId());

        LOGGER.info("editing user: " + user.toString());

        String newFirstName = RandomStringUtils.random(8, true, true);
        String newLastName = RandomStringUtils.random(8, true, true);

        clearFieldById("first-name");
        findElementById("first-name").sendKeys(newFirstName);
        clearFieldById("last-name");
        findElementById("last-name").sendKeys(newLastName);

        findButtonContainingText("Speichern").click();
        waitForURL(ManageUsersView.ROUTE);

        User savedUser = userRepository.findByEmailAddress(user.getEmailAddress()).get();
        assertNotNull(savedUser);
        LOGGER.info("checking firstName");
        assertTrue(newFirstName.equals(savedUser.getFirstName()));
        LOGGER.info("checking lastName");
        assertEquals(newLastName, savedUser.getLastName());
        LOGGER.info("checking password");
        assertEquals(user.getPassword(), savedUser.getPassword());
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testPasswordToShort() throws Exception {
        LOGGER.info("Testing create new User success");

        institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        login(UserType.SUPERADMIN);

        User user = new User();
        user.setEmailAddress(RandomStringUtils.random(8, true, true) + "@" + RandomStringUtils.random(8, true, true)
                + "." + RandomStringUtils.random(4, true, false));
        user.setFirstName(RandomStringUtils.random(8, true, true));
        user.setLastName(RandomStringUtils.random(8, true, true));
        user.setPassword(passwordEncoder.encode(RandomStringUtils.random(8, true, true)));
        user.setInstitutes(new HashSet<Institute>(instituteRepository.findAll()));
        user.setType(UserType.SUPERADMIN);

        userRepository.save(user);

        user = userRepository.findByEmailAddress(user.getEmailAddress()).get();

        goToURL(EditUserView.ROUTE + "/" + user.getId());

        LOGGER.info("editing user: " + user.toString());

        String newPassword = RandomStringUtils.random(4, true, true);

        findPasswordFieldById("password").sendKeys(newPassword);
        findPasswordFieldById("confirm-password").sendKeys(newPassword);

        findButtonContainingText("Speichern").click();
        timeoutWrongURL(ManageUsersView.ROUTE);
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
        goToURL(EditUserView.ROUTE + "/new");
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(EditUserView.ROUTE + "/new");
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(EditUserView.ROUTE + "/new", LecturesView.ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(EditUserView.ROUTE + "/new", LecturesView.ROUTE);
    }

}