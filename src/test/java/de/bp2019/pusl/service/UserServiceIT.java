package de.bp2019.pusl.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bp2019.pusl.config.TestUtils;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.UserRepository;

@SpringBootTest
public class UserServiceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceIT.class);

    @Autowired
    TestUtils testUtils;

    @Autowired
    InstituteRepository instituteRepository;
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

}