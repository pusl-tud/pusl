package de.bp2019.pusl.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

@SpringBootTest
public class InstituteServiceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstituteServiceIT.class);

    @Autowired
    InstituteService instituteService;
    
    @Autowired
    InstituteRepository instituteRepository;

    @MockBean
    UserService userService;

    @Test
    public void testSaveInstitute() throws Exception {
        LOGGER.info("Testing saveInstitute");

        Institute institute = new Institute(RandomStringUtils.randomAlphanumeric(8));

        LOGGER.info("saving as SUPERADMIN");
        when(userService.getCurrentUserType()).thenReturn(UserType.SUPERADMIN);
        instituteService.save(institute);
        assertTrue(instituteRepository.findByName(institute.getName()).isPresent());
        instituteRepository.deleteAll();

        LOGGER.info("saving as ADMIN");
        when(userService.getCurrentUserType()).thenReturn(UserType.ADMIN);
        assertThrows(UnauthorizedException.class, () ->  instituteService.save(institute));
        assertFalse(instituteRepository.findByName(institute.getName()).isPresent());

        LOGGER.info("saving as WIMI");
        when(userService.getCurrentUserType()).thenReturn(UserType.WIMI);
        assertThrows(UnauthorizedException.class, () ->  instituteService.save(institute));
        assertFalse(instituteRepository.findByName(institute.getName()).isPresent());

        LOGGER.info("saving as HIWI");
        when(userService.getCurrentUserType()).thenReturn(UserType.HIWI);
        assertThrows(UnauthorizedException.class, () ->  instituteService.save(institute));
        assertFalse(instituteRepository.findByName(institute.getName()).isPresent());
    }

    @BeforeEach
    public void cleanUp(){
        instituteRepository.deleteAll();
    }
}