package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import de.bp2019.zentraldatei.model.module.Module;

/**
 * Repository for access of {@link Module}s
 * 
 * @author Leon Chemnitz
 */
public interface ModuleRepository extends MongoRepository<Module, String> {
}