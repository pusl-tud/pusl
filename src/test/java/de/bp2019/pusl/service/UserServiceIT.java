package de.bp2019.pusl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bp2019.pusl.config.TestUtils;
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