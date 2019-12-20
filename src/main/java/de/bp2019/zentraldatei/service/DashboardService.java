// package de.bp2019.zentraldatei.service;

// import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Service;

// import de.bp2019.zentraldatei.model.ExerciseScheme;
// import de.bp2019.zentraldatei.model.Module;
// import de.bp2019.zentraldatei.model.User;

// @Service
// public class DashboardService {
//     private static final Logger LOGGER = LoggerFactory.getLogger(DashboardService.class);

//     private List<Module> recentModules;
//     private List<ExerciseScheme> recentExerciseSchemes;
//     private List<User> recentUsers;

//     public DashboardService() {
//     }

    
//     // /**
//     //  * add a Module to the list of recently viewed Modules
//     //  * 
//     //  * @param module
//     //  * @author Alexander Spaeth
//     //  */
//     // public void addRecentModules(Module module) {
//     //     recentModules.add(module);
//     // }

//     // /**
//     //  * add an ExerciseScheme to the list of recently viewed ExerciseSchemes
//     //  * 
//     //  * @param exerciseScheme
//     //  * @author Alexander Spaeth
//     //  */
//     // public void addRecentExerciseSchemes(ExerciseScheme exerciseScheme) {
//     //     recentExerciseSchemes.add(exerciseScheme);

//     // }

//     // /**
//     //  * add a User to the list of recently viewed Users
//     //  * 
//     //  * @param user
//     //  * @author Alexander Spaeth
//     //  */
//     // public void addRecentUsers(User user) {
//     //     recentUsers.add(user);
//     // }

//     // // TODO: How/When to remove things from the list of recently viewed items

//     // /**
//     //  * Get all Modules the User has recently viewed
//     //  * 
//     //  * @return
//     //  * @author Alexander Spaeth
//     //  */
//     // public List<Module> getRecentModules() {
//     //     return recentModules;
//     // }

//     // /**
//     //  * Get all ExerciseSchemes the User has recently viewed
//     //  * 
//     //  * @return
//     //  * @author Alexander Spaeth
//     //  */
//     // public List<ExerciseScheme> getRecentExcerciseSchemes() {
//     //     return recentExerciseSchemes;

//     // }

//     // /**
//     //  * Get all Users the User has recently viewed
//     //  * 
//     //  * @return
//     //  * @author Alexander Spaeth
//     //  */
//     // public List<User> getRecentUsers() {
//     //     return recentUsers;
//     // }

// }