package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.zentraldatei.model.ModuleScheme;

/**
 * Repository for access of ModuleSchemes
 * 
 * @author Leon Chemnitz
 */
public interface ModuleSchemeRepository extends MongoRepository<ModuleScheme, String> {
}