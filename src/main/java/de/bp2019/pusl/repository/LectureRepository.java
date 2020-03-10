package de.bp2019.pusl.repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;

/**
 * Repository for access of {@link Lecture}s
 * 
 * @author Leon Chemnitz
 */
public interface LectureRepository extends MongoRepository<Lecture, String> {
    Optional<Lecture> findByName(String name);
    Stream<Lecture> findByInstitutesIn(Set<Institute> institutes, Pageable pageable);
    int countByInstitutesIn(Set<Institute> institutes);
}