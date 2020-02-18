package de.bp2019.pusl.ui.views.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.views.BaseUITest;
import de.bp2019.pusl.ui.views.LoginViewIT;

public class EditUserViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Institute institute;

    @Test
    public void testCreateNewUserSuccess() throws Exception {
        LOGGER.info("Testing create new User");

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

        LOGGER.info("Testing creation of User: " + user.toString());

        findElementById("email-address").sendKeys(user.getEmailAddress());
        findElementById("first-name").sendKeys(user.getFirstName());
        findElementById("last-name").sendKeys(user.getLastName());
        findMSCBByIdAndSelectByTexts("institutes", Arrays.asList(institute.getName()));
        findSelectByIdAndSelectByText("user-type", user.getType().toString());
        findPasswordFieldById("password").sendKeys(user.getPassword());
        findPasswordFieldById("confirm-password").sendKeys(user.getPassword());


        findButtonContainingText("Speichern").click();
        waitForURL(ManageUsersView.ROUTE);

        User savedUser = userRepository.findByEmailAddress(user.getEmailAddress());
        assertNotNull(savedUser);
        assertEquals(user.getFirstName(), savedUser.getFirstName());
        assertEquals(user.getLastName(), savedUser.getLastName());
        assertTrue(passwordEncoder.matches(user.getPassword(), savedUser.getPassword())); 
        assertEquals(user.getType(), savedUser.getType()); 
        savedUser.getInstitutes().forEach(i -> assertEquals(institute.getName(), i.getName()));
        instituteRepository.deleteAll();
    }
 
}