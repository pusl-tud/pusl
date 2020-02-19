package de.bp2019.pusl.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.pusl.model.Institute;

/**
 * Repository for access of {@link Institute}s
 * 
 * @author Leon Chemnitz
 */
public interface InstituteRepository extends MongoRepository<Institute, String> {
    List<Institute> findByName(String name);
}