package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.zentraldatei.model.Institute;

/**
 * Repository for access of {@link Institute}s
 * 
 * @author Leon Chemnitz
 */
public interface InstituteRepository extends MongoRepository<Institute, String> {
}