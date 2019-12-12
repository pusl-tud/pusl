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
    /** Foreign Key - Institute.id */
    private Set<String> institutes;
    /** Foreign Key - User.id */
    private Set<String> hasAccess;
    /** Foreign Key ExerciseScheme.id */
    private List<String> exerciseSchemes;
    private String calculationRule;
    private List<PerformanceScheme> performanceSchemes;

    public ModuleScheme(String name, Set<String> institutes, Set<String> hasAccess,
            List<String> exerciseSchemes, String calculationRule) {
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

    public Set<String> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(Set<String> institutes) {
        this.institutes = institutes;
    }

    public List<String> getExerciseSchemes() {
        return exerciseSchemes;
    }

    public void setExerciseSchemes(List<String> exerciseSchemes) {
        this.exerciseSchemes = exerciseSchemes;
    }

    public String getCalculationRule() {
        return calculationRule;
    }

    public void setCalculationRule(String calculationRule) {
        this.calculationRule = calculationRule;
    }

    public Set<String> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(Set<String> hasAccess) {
        this.hasAccess = hasAccess;
    }
}