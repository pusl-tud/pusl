package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.zentraldatei.model.ModuleInstance;

/**
 * Repository for access of ModuleInstances
 * 
 * @author Leon Chemnitz
 */
public interface ModuleInstanceRepository extends MongoRepository<ModuleInstance, String> {
}