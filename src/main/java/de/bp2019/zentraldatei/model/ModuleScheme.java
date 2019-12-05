package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Set;

/**
 * A class to model a ModuleScheme. Consists of a name, a list of responsible
 * institutes, a list of exerciseSchemes, a grade, a calcuation rule and a list
 * of privileged users
 * 
 * @author Alex Späth
 */
@Document
public class ModuleScheme {

    @Id
    private String id;
    private String name;
    private Set<Institute> institutes;
    private Set<User> hasAccess;
    private List<ExerciseScheme> exerciseSchemes;
    private String calculationRule;

    public ModuleScheme(String id, String name, Set<Institute> institutes, Set<User> hasAccess,
            List<ExerciseScheme> exerciseSchemes, String calculationRule) {
        this.id = id;
        this.name = name;
        this.institutes = institutes;
        this.exerciseSchemes = exerciseSchemes;
        this.calculationRule = calculationRule;
        this.hasAccess = hasAccess;
    }

    public ModuleScheme() {
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

    public List<ExerciseScheme> getExerciseSchemes() {
        return exerciseSchemes;
    }

    public void setExerciseSchemes(List<ExerciseScheme> exerciseSchemes) {
        this.exerciseSchemes = exerciseSchemes;
    }

    public String getCalculationRule() {
        return calculationRule;
    }

    public void setCalculationRule(String calculationRule) {
        this.calculationRule = calculationRule;
    }

    public Set<User> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(Set<User> hasAccess) {
        this.hasAccess = hasAccess;
    }
}