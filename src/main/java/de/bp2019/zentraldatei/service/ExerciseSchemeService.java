package de.bp2019.zentraldatei.service;

import de.bp2019.zentraldatei.model.exercise.ExerciseScheme;
import de.bp2019.zentraldatei.repository.ExerciseSchemeRepository;
import de.bp2019.zentraldatei.repository.InstituteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
     * @return list of all exercise schemes
     * @author Leon Chemnitz
     */
    public List<ExerciseScheme> getAllExerciseSchemes() {
        // TODO: authorization
        return exerciseSchemeRepository.findAll();
    }

    /**
     * Persist one ExerciseScheme
     *
     * @param exerciseScheme to persist
     * @author Luca Dinies
     */
    public void saveExerciseScheme(ExerciseScheme exerciseScheme) {
        // TODO: Data Validation
        exerciseSchemeRepository.save(exerciseScheme);
    }

    /**
     * Update one ExerciseScheme in Database
     *
     * @param exerciseScheme to update
     * @author Luca Dinies
     */
    public void updateExerciseScheme(ExerciseScheme exerciseScheme) {
        // TODO: Data Validation
        exerciseSchemeRepository.save(exerciseScheme);
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
     * @param exerciseScheme to delete
     * @author Luca Dinies
     */
    public void deleteExerciseScheme(ExerciseScheme exerciseScheme){
        exerciseSchemeRepository.delete(exerciseScheme);
    }
}