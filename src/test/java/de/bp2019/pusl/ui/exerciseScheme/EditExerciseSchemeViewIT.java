package de.bp2019.pusl.ui.exerciseScheme;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.BaseUITest;
import de.bp2019.pusl.ui.LoginViewIT;
import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;
import de.bp2019.pusl.ui.views.user.EditUserView;
import de.bp2019.pusl.ui.views.user.ManageUsersView;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class EditExerciseSchemeViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewIT.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstituteRepository instituteRepository;

    private Institute institute;
    @Test
    public void testCreateNewUserSuccess() throws Exception {
        LOGGER.info("Testing create new ExerciseScheme success");

        institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);

        login(UserType.SUPERADMIN);
        goToURL(EditExerciseSchemeView.ROUTE + "/new");

    }



}