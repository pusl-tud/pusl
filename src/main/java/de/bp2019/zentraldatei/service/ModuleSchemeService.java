package de.bp2019.zentraldatei.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.ExcerciseScheme;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.model.User;

/**
 * Service providing relevant ModuleSchemes
 * 
 * @author Leon Chemnitz
 */
@Service
public class ModuleSchemeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleSchemeService.class);

    List<ModuleScheme> allModuleSchemes;

    @Autowired
    InstituteService instituteService;
    @Autowired
    UserService userService;
    @Autowired
    ExcerciseSchemeService excerciseSchemeService;

    public ModuleSchemeService() {

    }

    @PostConstruct
    private void PostConstruct(){
        LOGGER.debug("Started creation of ModuleSchemeService");

        /* Platzhalter Code da noch keine echten Repositories existieren */
        allModuleSchemes = new ArrayList<ModuleScheme>();

        Set<Institute> instituteSet1 = new HashSet<Institute>();
        instituteSet1.add(instituteService.getAllInstitutes().get(0));
        instituteSet1.add(instituteService.getAllInstitutes().get(2));

        Set<User> userSet1 = new HashSet<User>();
        userSet1.add(userService.getAllUsers().get(3));
        userSet1.add(userService.getAllUsers().get(5));
        userSet1.add(userService.getAllUsers().get(0));
        userSet1.add(userService.getAllUsers().get(1));

        List<ExcerciseScheme> excerciseSchemeList1 = new ArrayList<ExcerciseScheme>();
        excerciseSchemeList1.add(excerciseSchemeService.getAllExcerciseSchemes().get(0));     
        excerciseSchemeList1.add(excerciseSchemeService.getAllExcerciseSchemes().get(0));     
        excerciseSchemeList1.add(excerciseSchemeService.getAllExcerciseSchemes().get(0));     
        excerciseSchemeList1.add(excerciseSchemeService.getAllExcerciseSchemes().get(2));     

        allModuleSchemes.add(new ModuleScheme(
                "ASD986SFH9hASf",
                "Einführung in den Compilerbau",
                instituteSet1,
                excerciseSchemeList1,
                "Sehr simple Berechnungsregel",
                userSet1));


                
        Set<Institute> instituteSet2 = new HashSet<Institute>();
        instituteSet2.add(instituteService.getAllInstitutes().get(1));

        Set<User> userSet2 = new HashSet<User>();
        userSet2.add(userService.getAllUsers().get(2));
        userSet2.add(userService.getAllUsers().get(4));

        List<ExcerciseScheme> excerciseSchemeList2 = new ArrayList<ExcerciseScheme>();
        excerciseSchemeList2.add(excerciseSchemeService.getAllExcerciseSchemes().get(1));     
        excerciseSchemeList2.add(excerciseSchemeService.getAllExcerciseSchemes().get(0));     
        excerciseSchemeList2.add(excerciseSchemeService.getAllExcerciseSchemes().get(1));     
        excerciseSchemeList2.add(excerciseSchemeService.getAllExcerciseSchemes().get(2));     

        allModuleSchemes.add(new ModuleScheme(
                "SJDU=)jhAq987Sf",
                "Mathematik I",
                instituteSet2,
                excerciseSchemeList2,
                "ziemlich komplizierte Berechnungsregel",
                userSet2));

                
        Set<Institute> instituteSet3 = new HashSet<Institute>();
        instituteSet3.add(instituteService.getAllInstitutes().get(1));
        instituteSet3.add(instituteService.getAllInstitutes().get(0));
        instituteSet3.add(instituteService.getAllInstitutes().get(2));

        Set<User> userSet3 = new HashSet<User>();
        userSet3.add(userService.getAllUsers().get(1));
        userSet3.add(userService.getAllUsers().get(5));
        userSet3.add(userService.getAllUsers().get(0));

        List<ExcerciseScheme> excerciseSchemeList3 = new ArrayList<ExcerciseScheme>();
        excerciseSchemeList3.add(excerciseSchemeService.getAllExcerciseSchemes().get(1));  
        excerciseSchemeList3.add(excerciseSchemeService.getAllExcerciseSchemes().get(2));    
        excerciseSchemeList3.add(excerciseSchemeService.getAllExcerciseSchemes().get(0));     
        excerciseSchemeList3.add(excerciseSchemeService.getAllExcerciseSchemes().get(0));     
        excerciseSchemeList3.add(excerciseSchemeService.getAllExcerciseSchemes().get(1));     
        excerciseSchemeList3.add(excerciseSchemeService.getAllExcerciseSchemes().get(2));     

        allModuleSchemes.add(new ModuleScheme(
                "JOZso87qwkd0u",
                "Visuelle Trendanalyse",
                instituteSet3,
                excerciseSchemeList3,
                "wirklich sehr komplizierte Berechnungsregel",
                userSet3));

        LOGGER.debug("Finished creation of ModuleSchemeService");
    }

  /**
   * Get a ModuleScheme based on its Id.
   * Only return ModuleSchemes the User is authenticated to see.
   * @param Id Id to search for
   * @return found ModuleScheme with maching Id, null if none is found
   */
    public ModuleScheme getModuleSchemeById(String Id) {
        // TODO: Implement Authentication
        Optional<ModuleScheme> foundModuleScheme = allModuleSchemes.stream().filter(moduleScheme -> moduleScheme.getId().equals(Id)).findFirst();
        
        if(foundModuleScheme.isPresent()){
            return foundModuleScheme.get();
        }else{
            return null;
        }
    }

    /**
     * Get All ModuleSchemes the User is authenticated to see.
     * @return
     */
    public List<ModuleScheme> getAllModuleSchemes() {
        // TODO: Implement Authentication
        return allModuleSchemes;
    }

    public void addModuleScheme(ModuleScheme moduleScheme){
        // TODO: Data Validation

        moduleScheme.setId(getNewId());
        allModuleSchemes.add(moduleScheme);
    }

    public void updateModuleScheme(ModuleScheme moduleScheme){
        // TODO: Data Validation
        LOGGER.info(moduleScheme.getId());
        allModuleSchemes.removeIf(item -> item.getId().equals(moduleScheme.getId()));
        allModuleSchemes.add(moduleScheme);
    }

    private String getNewId(){
        // TODO: Implement correct Id generation

        byte[] array = new byte[7];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}