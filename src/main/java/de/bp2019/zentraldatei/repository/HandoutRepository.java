package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.zentraldatei.model.Handout;

/**
 * Repository for access of {@link Handout}s
 * 
 * @author Leon Chemnitz
 */
public interface HandoutRepository extends MongoRepository<Handout, String> {
}