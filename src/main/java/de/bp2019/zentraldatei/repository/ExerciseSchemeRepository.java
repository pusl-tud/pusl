package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.zentraldatei.model.ExerciseScheme;

/**
 * Repository for access of ExerciseSchemes
 * 
 * @author Leon Chemnitz
 */
public interface ExerciseSchemeRepository extends MongoRepository<ExerciseScheme, String> {
}