package de.bp2019.zentraldatei.service;

import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.repository.ExerciseSchemeRepository;
import de.bp2019.zentraldatei.repository.InstituteRepository;
import de.bp2019.zentraldatei.repository.ModuleSchemeRepository;
import de.bp2019.zentraldatei.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service providing relevant ModuleSchemes
 * 
 * @author Leon Chemnitz
 */
@Service
public class ModuleSchemeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleSchemeService.class);

    @Autowired
    ModuleSchemeRepository moduleSchemeRepository;
    @Autowired
    InstituteRepository instituteRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    public ModuleSchemeService() {
    }

    /**
     * Get a ModuleScheme based on its Id. Only return ModuleSchemes the User is
     * authenticated to see.
     * 
     * @param id Id to search for
     * @return found ModuleScheme with maching Id, null if none is found
     * @author Leon Chemnitz
     */
    public ModuleScheme getModuleSchemeById(String id) {
        // TODO: Implement Authentication

        Optional<ModuleScheme> foundModuleScheme = moduleSchemeRepository.findById(id);

        if (foundModuleScheme.isPresent()) {
            return foundModuleScheme.get();
        } else {
            return null;
        }
    }

    /**
     * Get all ModuleSchemes the User is authenticated to see.
     * 
     * @return list of al module schemes
     * @author Leon Chemnitz
     */
    public List<ModuleScheme> getAllModuleSchemes() {
        // TODO: Implement Authentication
        return moduleSchemeRepository.findAll();
    }

    /**
     * Persist one ModuleScheme
     *
     * @param moduleScheme to persist
     * @author Leon Chemnitz
     */
    public void saveModuleScheme(ModuleScheme moduleScheme) {
        // TODO: Data Validation
        moduleSchemeRepository.save(moduleScheme);
    }

    /**
     * Update one ModuleScheme in Database
     * 
     * @param moduleScheme to update
     * @author Leon Chemnitz
     */
    public void updateModuleScheme(ModuleScheme moduleScheme) {
        // TODO: Data Validation
        moduleSchemeRepository.save(moduleScheme);
    }

    /**
     * Get the Institutes asociated with a ModuleScheme as a Set. This method is
     * neccesairy because in a ModuleScheme instance only the Institute Ids are
     * referenced.
     * 
     * @param moduleScheme module scheme
     * @return Set of Institute instances asociated with ModuleScheme
     * @author Leon Chemnitz
     */
    public Set<Institute> getInstitutes(ModuleScheme moduleScheme) {
        if (moduleScheme.getInstitutes() == null) {
            return new HashSet<Institute>();
        } else {
            Iterable<Institute> institutes = instituteRepository.findAllById(moduleScheme.getInstitutes());
            return StreamSupport.stream(institutes.spliterator(), false).collect(Collectors.toSet());
        }
    }

    /**
     * Set the Institutes asociated with a ModuleScheme. This method is neccesairy
     * because in a ModuleScheme instance only the Institute Ids are referenced.
     * 
     * @param moduleScheme module scheme
     * @param institutes Set of Institute instances asociated with ModuleScheme
     * @author Leon Chemnitz
     */
    public void setInstitutes(ModuleScheme moduleScheme, Set<Institute> institutes) {
        Set<String> idSet = institutes.stream().map(Institute::getId).collect(Collectors.toSet());
        moduleScheme.setInstitutes(idSet);
    }

    /**
     * Get the Users which have access to a ModuleScheme as a Set. This method is
     * neccesairy because in a ModuleScheme instance only the User Ids are
     * referenced.
     * 
     * @param moduleScheme module scheme
     * @return Set of User instances that have access to the ModuleScheme
     * @author Leon Chemnitz
     */
    public Set<User> getHasAccess(ModuleScheme moduleScheme) {
        if (moduleScheme.getHasAccess() == null) {
            return new HashSet<User>();
        } else {
            Iterable<User> users = userRepository.findAllById(moduleScheme.getHasAccess());
            return StreamSupport.stream(users.spliterator(), false).collect(Collectors.toSet());
        }
    }

    /**
     * Set the Users which have access to a ModuleScheme. This method is neccesairy
     * because in a ModuleScheme instance only the User Ids are referenced.
     * 
     * @param moduleScheme module scheme
     * @param hasAccessSet Set of User instances that have access to the ModuleScheme
     * @author Leon Chemnitz
     */
    public void setHasAccess(ModuleScheme moduleScheme, Set<User> hasAccessSet) {
        Set<String> idSet = hasAccessSet.stream().map(User::getId).collect(Collectors.toSet());
        moduleScheme.setHasAccess(idSet);
    }

    /**
     * Get the ExerciseSchemes asociated with a ModuleScheme as a List. This method
     * is neccesairy because in a ModuleScheme instance only the ExerciseScheme Ids
     * are referenced.
     * 
     * @param moduleScheme module scheme
     * @return List of ExerciseScheme instances asociated with ModuleScheme
     * @author Leon Chemnitz
     */
    public List<ExerciseScheme> getExerciseSchemes(ModuleScheme moduleScheme) {
        List<ExerciseScheme> exerciseSchemes = new ArrayList<>();

        if (moduleScheme.getExerciseSchemes() == null) {
            return exerciseSchemes;
        }

        moduleScheme.getExerciseSchemes().stream().forEach(id -> {
            Optional<ExerciseScheme> exerciseScheme = exerciseSchemeRepository.findById(id);
            if (exerciseScheme.isPresent()) {
                exerciseSchemes.add(exerciseScheme.get());
            } else {
                LOGGER.warn("Tried to find ExerciseScheme which does not exist in repository! Id was: " + id);
            }
        });

        return exerciseSchemes;
    }

    /**
     * Set the ExerciseSchemes asociated with a ModuleScheme. This method is
     * neccesairy because in a ModuleScheme instance only the ExerciseScheme Ids are
     * referenced.
     * 
     * @param moduleScheme module scheme
     * @param exerciseSchemeList Set of ExerciseScheme instances asociated with ModuleScheme
     * @author Leon Chemnitz
     */
    public void setExerciseSchemes(ModuleScheme moduleScheme, List<ExerciseScheme> exerciseSchemeList) {
        List<String> idList = exerciseSchemeList.stream().map(ExerciseScheme::getId).collect(Collectors.toList());
        moduleScheme.setExerciseSchemes(idList);
    }

    /**
     * Delete a ModuleScheme
     *
     * @param moduleScheme to delete
     * @author Luca Dinies
     */
    public void deleteModuleScheme(ModuleScheme moduleScheme) {
        moduleSchemeRepository.delete(moduleScheme);
    }
}