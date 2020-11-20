package de.bp2019.pusl.repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.pusl.model.ExerciseScheme;

/**
 * Repository for access of {@link ExerciseScheme}s
 * 
 * @author Leon Chemnitz
 */
public interface ExerciseSchemeRepository extends MongoRepository<ExerciseScheme, ObjectId> {
    Optional<ExerciseScheme> findByName(String name);
    Stream<ExerciseScheme> findByInstitutesIn(Set<ObjectId> institutes, Pageable pageable);
    int countByInstitutesIn(Set<ObjectId> institutes);
}