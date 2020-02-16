package de.bp2019.pusl.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model of a Lecture
 * 
 * @author Leon Chemnitz
 */
@Document
public class Lecture {

    @Id
    private ObjectId id;

    private String name;

    @DBRef
    private Set<Institute> institutes;

    @DBRef
    private Set<User> hasAccess;

    private List<Exercise> exercises;

    private List<PerformanceScheme> performanceSchemes;

    private Instant lastModified;

    public Lecture(String name, Set<Institute> institutes, Set<User> hasAccess, List<Exercise> exercises,
            List<PerformanceScheme> performanceSchemes) {
        this.name = name;
        this.institutes = institutes;
        this.exercises = exercises;
        this.hasAccess = hasAccess;
        this.performanceSchemes = performanceSchemes;
        this.lastModified = Instant.now();
    }

    public Lecture() {
        this.lastModified = Instant.now();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Institute> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(Set<Institute> institutes) {
        this.institutes = institutes;
    }

    public Set<User> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(Set<User> hasAccess) {
        this.hasAccess = hasAccess;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public List<PerformanceScheme> getPerformanceSchemes() {
        return performanceSchemes;
    }

    public void setPerformanceSchemes(List<PerformanceScheme> performanceSchemes) {
        this.performanceSchemes = performanceSchemes;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "Lecture [institutes=" + institutes + ", lastModified=" + lastModified + ", name=" + name
                + ", performanceSchemes=" + performanceSchemes + "]";
    }

}