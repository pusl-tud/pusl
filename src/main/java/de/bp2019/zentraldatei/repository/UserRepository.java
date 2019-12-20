package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.zentraldatei.model.User;

/**
 * Repository for access of {@link User}s
 * 
 * @author Leon Chemnitz
 */
public interface UserRepository extends MongoRepository<User, String> {
}