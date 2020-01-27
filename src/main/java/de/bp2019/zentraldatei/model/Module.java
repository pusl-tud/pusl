package de.bp2019.zentraldatei.model;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A class to model a Module. Consists of a name, a list of responsible
 * {@link institute}s, a list of {@link Exercise}s, a {@link Grade}, a
 * calcuation rule and a list of privileged {@link user}s
 * 
 * @author Alex Späth
 */
@Document
public class Module {

    @Id
    private ObjectId id;
    private String name;
    @DBRef
    private Set<Institute> institutes;
    @DBRef
    private Set<User> hasAccess;
    private List<Exercise> exercises;

    private List<PerformanceScheme> performanceSchemes;

    /** Temporary field TODO: replace with PerformanceSchemes */
    private String calculationRule;

    public Module(String name, Set<Institute> institutes, Set<User> hasAccess, List<Exercise> exercises,
            String calculationRule) {
        this.name = name;
        this.institutes = institutes;
        this.exercises = exercises;
        this.calculationRule = calculationRule;
        this.hasAccess = hasAccess;
    }

    public Module() {
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

    public String getCalculationRule() {
        return calculationRule;
    }

    public void setCalculationRule(String calculationRule) {
        this.calculationRule = calculationRule;
    }

}