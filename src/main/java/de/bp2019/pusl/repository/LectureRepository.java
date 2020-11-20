package de.bp2019.pusl.repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.pusl.model.Lecture;

/**
 * Repository for access of {@link Lecture}s
 * 
 * @author Leon Chemnitz
 */
public interface LectureRepository extends MongoRepository<Lecture, ObjectId> {
    Optional<Lecture> findByName(String name);
    Stream<Lecture> findByInstitutesIn(Set<ObjectId> institutes, Pageable pageable);
    Stream<Lecture> findByInstitutesInAndHasAccessIn(Set<ObjectId> institutes, ObjectId user, Pageable pageable);
    int countByInstitutesIn(Set<ObjectId> institutes);
    int countByInstitutesInAndHasAccessIn(Set<ObjectId> institutes, ObjectId user);
}