package de.bp2019.zentraldatei.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.repository.ExerciseSchemeRepository;

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
     * Get a ExerciseScheme based on its Id. Only returns ExerciseSchemes the User
     * is authenticated to see.
     * 
     * @param Id Id to search for
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

}