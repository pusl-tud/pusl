package de.bp2019.zentraldatei.service;

import de.bp2019.zentraldatei.model.exercise.ExerciseInstance;
import de.bp2019.zentraldatei.repository.ExerciseInstanceRepository;
import de.bp2019.zentraldatei.repository.InstituteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service providing relevant {@link ExerciseInstance}s
 * 
 * @author Leon Chemnitz
 */
@Service
public class ExerciseInstanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseInstanceService.class);

    @Autowired
    ExerciseInstanceRepository exerciseInstanceRepository;

    @Autowired
    InstituteRepository instituteRepository;

    public ExerciseInstanceService() {
    }

    /**
     * Get all ExerciseInstances the User is authenticated to see.
     * 
     * @return list of all exercise schemes
     * @author Leon Chemnitz
     */
    public List<ExerciseInstance> getAllExerciseInstances() {
        // TODO: authorization
        return exerciseInstanceRepository.findAll();
    }

    /**
     * Persist one ExerciseInstance
     *
     * @param exerciseInstance to persist
     * @author Leon Chemnitz
     */
    public void saveExerciseInstance(ExerciseInstance exerciseInstance) {
        // TODO: Data Validation
        exerciseInstanceRepository.save(exerciseInstance);
    }

    /**
     * Update one ExerciseInstance in Database
     *
     * @param exerciseInstance to update
     * @author Leon Chemnitz
     */
    public void updateExerciseInstance(ExerciseInstance exerciseInstance) {
        // TODO: Data Validation
        exerciseInstanceRepository.save(exerciseInstance);
    }


    /**
     * Get a ExerciseInstance based on its Id. Only returns ExerciseInstances the User
     * is authenticated to see.
     * 
     * @param id Id to search for
     * @return found ExerciseInstance with maching Id, null if none is found
     * @author Leon Chemnitz
     */

    public ExerciseInstance getExerciseInstanceById(String id) {
        // TODO: authorization
        Optional<ExerciseInstance> foundExerciseInstance = exerciseInstanceRepository.findById(id);

        if (foundExerciseInstance.isPresent()) {
            return foundExerciseInstance.get();
        } else {
            LOGGER.warn("Tried to get ExerciseInstance which doesn't exist in Database! ExerciseInstance ID was: " + id);
            return null;
        }
    }

    /**
     * Delete an ExerciseInstance
     *
     * @param exerciseInstance to delete
     * @author Leon Chemnitz
     */
    public void deleteExerciseInstance(ExerciseInstance exerciseInstance){
        exerciseInstanceRepository.delete(exerciseInstance);
    }
}