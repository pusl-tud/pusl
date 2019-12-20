package de.bp2019.zentraldatei.model.module;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.model.exercise.ExerciseInstance;

import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Set;

/**
 * A class to model a Module. Consists of a name, a list of responsible
 * {@link institute}s, a list of {@link ExerciseInstance}s, a {@link Grade}, a
 * calcuation rule and a list of privileged {@link user}s
 * 
 * @author Alex Späth
 */
@Document
public class Module {

    @Id
    private String id;
    private String name;
    @DBRef
    private Set<Institute> institutes;
    @DBRef
    private Set<User> hasAccess;
    @DBRef
    private List<ExerciseInstance> exercises;

    private List<PerformanceScheme> performanceSchemes;

    /** Temporary field TODO: replace with PerformanceSchemes */
    private String calculationRule;

    public Module(String name, Set<Institute> institutes, Set<User> hasAccess, List<ExerciseInstance> exercises,
            String calculationRule) {
        this.name = name;
        this.institutes = institutes;
        this.exercises = exercises;
        this.calculationRule = calculationRule;
        this.hasAccess = hasAccess;
    }

    public Module() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public List<ExerciseInstance> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseInstance> exercises) {
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