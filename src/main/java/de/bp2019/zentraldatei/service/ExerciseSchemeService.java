package de.bp2019.zentraldatei.service;

import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.repository.ExerciseSchemeRepository;
import de.bp2019.zentraldatei.repository.InstituteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service providing relevant ExerciseSchemes
 * 
 * @author Leon Chemnitz
 */
@Service
public class ExerciseSchemeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseSchemeService.class);

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    InstituteRepository instituteRepository;

    public ExerciseSchemeService() {
    }

    /**
     * Get all ExerciseSchemes the User is authenticated to see.
     * 
     * @author Leon Chemnitz
     */
    public List<ExerciseScheme> getAllExerciseSchemes() {
        // TODO: authorization
        return exerciseSchemeRepository.findAll();
    }

    /**
     * Persist one ExerciseScheme
     *
     * @author Luca Dinies
     */
    public void saveModuleScheme(ExerciseScheme exerciseScheme) {
        // TODO: Data Validation
        exerciseSchemeRepository.save(exerciseScheme);
    }

    /**
     * Update one ExerciseScheme in Database
     *
     * @author Luca Dinies
     */
    public void updateModuleScheme(ExerciseScheme exerciseScheme) {
        // TODO: Data Validation
        exerciseSchemeRepository.save(exerciseScheme);
    }

    /**
     * Get the Institutes associated with a ExerciseScheme as a Set. This method is
     * necessary because in a ExerciseScheme instance only the Institute Ids are
     * referenced.
     *
     * @param exerciseScheme
     * @return Set of Institute instances associated with ModuleScheme
     * @author Luca Dinies
     */
    public Set<Institute> getInstitutes(ExerciseScheme exerciseScheme) {
        if (exerciseScheme.getInstitutes() == null) {
            return new HashSet<Institute>();
        } else {
            Iterable<Institute> institutes = instituteRepository.findAllById(exerciseScheme.getInstitutes());
            return StreamSupport.stream(institutes.spliterator(), false).collect(Collectors.toSet());
        }
    }

    /**
     * Get the Institutes associated with a ExerciseScheme as a Set. This method is
     * necessary because in a ExerciseScheme instance only the Institute Ids are
     * referenced.
     *
     * @param exerciseScheme
     * @return Set of Institute instances associated with ModuleScheme
     * @author Luca Dinies
     */
    public Set<String> getTokens(ExerciseScheme exerciseScheme) {
        return exerciseScheme.getTokens();
    }

    /**
     * Get a ExerciseScheme based on its Id. Only returns ExerciseSchemes the User
     * is authenticated to see.
     * 
     * @param id Id to search for
     * @return found ExerciseScheme with maching Id, null if none is found
     * @author Leon Chemnitz
     */

    public ExerciseScheme getExerciseSchemeById(String id) {
        // TODO: authorization
        Optional<ExerciseScheme> foundExerciseScheme = exerciseSchemeRepository.findById(id);

        if (foundExerciseScheme.isPresent()) {
            return foundExerciseScheme.get();
        } else {
            LOGGER.warn("Tried to get ExerciseScheme which doesn't exist in Database! ExerciseScheme ID was: " + id);
            return null;
        }
    }

    /**
     * Delete an ExerciseScheme
     *
     * @author Luca Dinies
     */

    public void deleteExerciseScheme(ExerciseScheme exerciseScheme){
        exerciseSchemeRepository.delete(exerciseScheme);
    }
}