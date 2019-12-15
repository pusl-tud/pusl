package de.bp2019.zentraldatei.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.model.User;


@Service
public class DashboardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardService.class);

    private List<ModuleScheme> recentModuleSchemes;
    private List<ExerciseScheme> recentExerciseSchemes;
    private List<User> recentUsers;
    
    
    public DashboardService() {
    }
    
    /**
     * add a ModuleScheme to the list of recently viewed ModuleSchemes
     * 
     * @author Alexander Spaeth
     */
    public void addRecentModuleSchemes(ModuleScheme moduleScheme) {
        recentModuleSchemes.add(moduleScheme);
    }
    
    /**
     * add an ExerciseScheme to the list of recently viewed ExerciseSchemes
     * 
     * @author Alexander Spaeth
     */
    public void addRecentExerciseSchemes(ExerciseScheme exerciseScheme) {
        recentExerciseSchemes.add(exerciseScheme);
        
    }
    
    /**
     * add a User to the list of recently viewed Users
     * 
     * @author Alexander Spaeth
     */
    public void addRecentUsers(User user) {
        recentUsers.add(user);
    }
    
    // TODO: How/When to remove things from the list of recently viewed items

    /**
     * Get all ModuleSchemes the User has recently viewed
     * 
     * @author Alexander Spaeth
     */
    public List<ModuleScheme> getRecentModuleSchemes() {
        return recentModuleSchemes;
    }
    
    /**
     * Get all ExerciseSchemes the User has recently viewed
     * 
     * @author Alexander Spaeth
     */
    public List<ExerciseScheme> getRecentExcerciseSchemes() {
        return recentExerciseSchemes;
        
    }
    
    /**
     * Get all Users the User has recently viewed
     * 
     * @author Alexander Spaeth
     */
    public List<User> getRecentUsers() {
        return recentUsers;
    }

 
}