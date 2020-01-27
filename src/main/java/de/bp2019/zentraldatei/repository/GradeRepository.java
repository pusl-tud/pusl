package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.zentraldatei.model.Grade;

/**
 * Repository for access of {@link Grade}s
 * 
 * @author Leon Chemnitz
 */
public interface GradeRepository extends MongoRepository<Grade, String> {
}