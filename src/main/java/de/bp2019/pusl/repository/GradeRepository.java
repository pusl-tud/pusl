package de.bp2019.pusl.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.pusl.model.Grade;

/**
 * Repository for access of {@link Grade}s
 * 
 * @author Leon Chemnitz
 */
public interface GradeRepository extends MongoRepository<Grade, String> {
}