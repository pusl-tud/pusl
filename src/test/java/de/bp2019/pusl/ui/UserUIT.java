package de.bp2019.pusl.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.bp2019.pusl.config.BaseUIT;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.views.user.EditUserView;
import de.bp2019.pusl.ui.views.user.ManageUsersView;

/**
 * UI test for {@link EditUserView} and {@link ManageUsersView}
 * 
 * @author Leon Chemnitz
 */
public class UserUIT extends BaseUIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserUIT.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Institute institute;

    private void goToManageUsersView() throws InterruptedException {
        findElementById("user-menu-button").click();
        waitForURL(ManageUsersView.ROUTE);
    }

    private void goToNewUser() throws InterruptedException {
        goToManageUsersView();
        findElementById("new-user-button").click();

        waitForURL(EditUserView.ROUTE + "/new");
    }

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
        goToNewUser();

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
        instituteRepository.findAllById(savedUser.getInstitutes())
                .forEach(i -> assertEquals(institute.getName(), i.getName()));
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
        user.setInstitutes(instituteRepository.findAll().stream().map(Institute::getId).collect(Collectors.toSet()));
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
        user.setInstitutes(instituteRepository.findAll().stream().map(Institute::getId).collect(Collectors.toSet()));
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
    public void testNameButton() throws Exception {
        LOGGER.info("Testing User name button");

        User user = testUtils.createUser(UserType.ADMIN);
        String id = user.getId().toString();
        String name = user.getFullName();

        login(UserType.SUPERADMIN);
        goToManageUsersView();

        /* click name button */
        findButtonContainingText(name).click();

        waitForURL(EditUserView.ROUTE + "/" + id);
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testDeleteButton() throws Exception {
        LOGGER.info("Testing Delete User Button");

        User user = testUtils.createUser(UserType.ADMIN);
        String id = user.getId().toString();
        String email = user.getEmailAddress();

        login(UserType.SUPERADMIN);
        goToManageUsersView();

        /* click delete button */
        findElementById("delete-" + id).click();

        acceptConfirmDeletionDialog(email);

        assertTrue(userRepository.findByEmailAddress(user.getEmailAddress()).isEmpty());
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testEmailDuplicate() throws Exception {
        LOGGER.info("Testing create new User with assigned Email");

        institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        User user = new User();
        user.setEmailAddress(RandomStringUtils.random(8, true, true) + "@" + RandomStringUtils.random(8, true, true)
                + "." + RandomStringUtils.random(4, true, false));
        user.setFirstName(RandomStringUtils.random(8, true, true));
        user.setLastName(RandomStringUtils.random(8, true, true));
        user.setPassword(RandomStringUtils.random(8, true, true));
        user.setType(UserType.SUPERADMIN);

        userRepository.save(user);

        login(UserType.SUPERADMIN);
        goToNewUser();

        findElementById("email-address").sendKeys(user.getEmailAddress());
        findElementById("first-name").sendKeys(user.getFirstName());
        findElementById("last-name").sendKeys(user.getLastName());
        findMSCBByIdAndSelectByTexts("institutes", Arrays.asList(institute.getName()));
        findSelectByIdAndSelectByText("user-type", user.getType().toString());
        findPasswordFieldById("password").sendKeys(user.getPassword());
        findPasswordFieldById("confirm-password").sendKeys(user.getPassword());

        findButtonContainingText("Speichern").click();
        timeoutWrongURL(ManageUsersView.ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testAutorisationWithParameters() throws Exception {
        LOGGER.info("Testing autorisation for query Parameters");

        User user = new User();
        user.setEmailAddress(RandomStringUtils.random(8, true, true) + "@" + RandomStringUtils.random(8, true, true)
                + "." + RandomStringUtils.random(4, true, false));
        user.setFirstName(RandomStringUtils.random(8, true, true));
        user.setLastName(RandomStringUtils.random(8, true, true));
        user.setPassword(RandomStringUtils.random(8, true, true));
        user.setType(UserType.SUPERADMIN);

        userRepository.save(user);

        ObjectId id = userRepository.findByEmailAddress(user.getEmailAddress()).get().getId();

        login(UserType.HIWI);

        goToURLandWaitForRedirect(EditUserView.ROUTE + "/" + id.toString(), PuslProperties.ROOT_ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testWrongParameters() throws Exception {
        LOGGER.info("Testing wrong query Parameters");

        login(UserType.SUPERADMIN);

        goToURLandWaitForRedirect(EditUserView.ROUTE + "/" + RandomStringUtils.randomAlphanumeric(10), PuslProperties.ROOT_ROUTE);
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccessManageUsersView() throws Exception {
        LOGGER.info("Testing access ManageUsersView");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(ManageUsersView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(ManageUsersView.ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(ManageUsersView.ROUTE, PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccessEditUserView() throws Exception {
        LOGGER.info("Testing access EditUserView");

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
        goToURLandWaitForRedirect(EditUserView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(EditUserView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
    }
}