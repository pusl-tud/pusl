package de.bp2019.pusl.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.pusl.model.Lecture;

/**
 * Repository for access of {@link Lecture}s
 * 
 * @author Leon Chemnitz
 */
public interface LectureRepository extends MongoRepository<Lecture, String> {
}