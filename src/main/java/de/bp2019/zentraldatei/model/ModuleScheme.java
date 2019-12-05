package de.bp2019.zentraldatei.model;

import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class ModuleScheme {
    @Id
    private String id;
    private String name;
    private Set<Institute> institutes;
    private List<ExcerciseScheme> excerciseSchemes;
    private String calculationRule;
    private Set<User> hasAccess;

    public ModuleScheme(String id, String name, Set<Institute> institutes, List<ExcerciseScheme> excerciseSchemes,
            String calculationRule, Set<User> hasAccess) {
        this.id = id;
        this.name = name;
        this.institutes = institutes;
        this.excerciseSchemes = excerciseSchemes;
        this.calculationRule = calculationRule;
        this.hasAccess = hasAccess;
    }

    public ModuleScheme(){
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

    public List<ExcerciseScheme> getExcerciseSchemes() {
        return excerciseSchemes;
    }

    public void setExcerciseSchemes(List<ExcerciseScheme> excerciseSchemes) {
        this.excerciseSchemes = excerciseSchemes;
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

    public Set<Institute> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(Set<Institute> institutes) {
        this.institutes = institutes;
    }

    @Override
    public String toString() {
        return "ModuleSchemeTmp [calculationRule=" + calculationRule + ", excerciseSchemes=" + excerciseSchemes
                + ", hasAccess=" + hasAccess + ", institutes=" + institutes + ", name=" + name + "]";
    }

}