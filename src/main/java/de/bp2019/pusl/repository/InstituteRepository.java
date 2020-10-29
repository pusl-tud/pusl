package de.bp2019.pusl.repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.pusl.model.Institute;

/**
 * Repository for access of {@link Institute}s
 * 
 * @author Leon Chemnitz
 */
public interface InstituteRepository extends MongoRepository<Institute, String> {
    Optional<Institute> findByName(String name);
    Stream<Institute> findByIdIn(Set<ObjectId> ids, Pageable pageable);
    Set<Institute> findAllByIdIn(Set<ObjectId> ids);
}