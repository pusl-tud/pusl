package de.bp2019.pusl.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;

/**
 * Repository for access of {@link Grade}s
 * 
 * @author Leon Chemnitz
 */
public interface GradeRepository extends MongoRepository<Grade, String> {
    List<Grade> getAllGradesByLecture(Lecture lecture);
}