// package de.bp2019.pusl.service;

// import static org.junit.Assert.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;

// import org.apache.commons.lang3.RandomStringUtils;
// import org.junit.jupiter.api.Test;
// import org.mockito.internal.util.collections.Sets;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import de.bp2019.pusl.config.TestUtils;
// import de.bp2019.pusl.enums.UserType;
// import de.bp2019.pusl.model.ExerciseScheme;
// import de.bp2019.pusl.model.Institute;
// import de.bp2019.pusl.model.User;
// import de.bp2019.pusl.repository.ExerciseSchemeRepository;
// import de.bp2019.pusl.repository.InstituteRepository;
// import de.bp2019.pusl.repository.UserRepository;
// import de.bp2019.pusl.util.exceptions.UnauthorizedException;

// /**
//  * @author Leon Chemnitz
//  */
// @SpringBootTest
// public class ExerciseSchemeServiceIT {
//     private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceIT.class);

//     @Autowired
//     TestUtils testUtils;

//     @Autowired
//     InstituteRepository instituteRepository;

//     @Autowired
//     UserRepository userRepository;

//     @Autowired
//     ExerciseSchemeRepository exerciseSchemeRepository;

//     @Autowired
//     ExerciseSchemeService exerciseSchemeService;

//     /**
//      * @author Leon Chemnitz
//      */
//     @Test
//     public void testDelete() throws Exception {
//         LOGGER.info("testing delete");

//         ExerciseScheme exerciseScheme = new ExerciseScheme(RandomStringUtils.randomAlphanumeric(1, 16), false, 0, null,
//                 null, null, null);

//         LOGGER.info("testing as SUPERADMIN");
//         exerciseSchemeRepository.deleteAll();
//         exerciseSchemeRepository.save(exerciseScheme);
//         testUtils.authenticateAs(UserType.SUPERADMIN);
//         exerciseSchemeService.delete(exerciseScheme);
//         assertEquals(0, exerciseSchemeRepository.count());

//         LOGGER.info("testing as HIWI");
//         exerciseSchemeRepository.deleteAll();
//         exerciseSchemeRepository.save(exerciseScheme);
//         testUtils.authenticateAs(UserType.HIWI);
//         assertThrows(UnauthorizedException.class, () -> exerciseSchemeService.delete(exerciseScheme));        
//         assertEquals(1, exerciseSchemeRepository.count());

//         LOGGER.info("test successful");
//     }

//     /**
//      * @author Leon Chemnitz
//      */
//     @Test
//     public void testSave() throws Exception {
//         LOGGER.info("testing save");

//         Institute institute = new Institute();
//         institute.setName(RandomStringUtils.randomAlphanumeric(1, 16));
//         instituteRepository.save(institute);

//         ExerciseScheme exerciseScheme = new ExerciseScheme();
//         exerciseScheme.setName(RandomStringUtils.randomAlphanumeric(1, 16));
//         exerciseScheme.setInstitutes(Sets.newSet(institute));

//         LOGGER.info("testing as SUPERADMIN");
//         exerciseSchemeRepository.deleteAll();
//         testUtils.authenticateAs(UserType.SUPERADMIN);
//         exerciseSchemeService.save(exerciseScheme);
//         assertEquals(1, exerciseSchemeRepository.count());

//         LOGGER.info("testing as ADMIN unauthorized");
//         exerciseSchemeRepository.deleteAll();
//         User admin = testUtils.authenticateAs(UserType.ADMIN);
//         assertThrows(UnauthorizedException.class, () -> exerciseSchemeService.save(exerciseScheme));
//         assertEquals(0, exerciseSchemeRepository.count());

//         LOGGER.info("testing as WIMI unauthorized");
//         exerciseSchemeRepository.deleteAll();
//         User wimi = testUtils.createUser(UserType.WIMI);
//         wimi.setInstitutes(Sets.newSet(institute));
//         testUtils.authenticateAs(wimi);
//         assertThrows(UnauthorizedException.class, () -> exerciseSchemeService.save(exerciseScheme));
//         assertEquals(0, exerciseSchemeRepository.count());

//         LOGGER.info("testing as HIWI unauthorized");
//         exerciseSchemeRepository.deleteAll();
//         User hiwi = testUtils.createUser(UserType.HIWI);
//         hiwi.setInstitutes(Sets.newSet(institute));
//         testUtils.authenticateAs(hiwi);
//         assertThrows(UnauthorizedException.class, () -> exerciseSchemeService.save(exerciseScheme));
//         assertEquals(0, exerciseSchemeRepository.count());

//         LOGGER.info("testing as ADMIN authorized");
//         exerciseSchemeRepository.deleteAll();
//         admin.setInstitutes(Sets.newSet(institute));
//         userRepository.save(admin);
//         testUtils.authenticateAs(admin);
//         exerciseSchemeService.save(exerciseScheme);
//         assertEquals(1, exerciseSchemeRepository.count());

//         LOGGER.info("test successful");
//     }
// }