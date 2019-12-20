package de.bp2019.zentraldatei.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.zentraldatei.model.exercise.ExerciseInstance;
import de.bp2019.zentraldatei.model.module.Module;

/**
 * Repository for access of {@link ExerciseInstance}s
 * 
 * @author Leon Chemnitz
 */
public interface ExerciseInstanceRepository extends MongoRepository<ExerciseInstance, String> {
    List<ExerciseInstance> findByModule(Module module);
}