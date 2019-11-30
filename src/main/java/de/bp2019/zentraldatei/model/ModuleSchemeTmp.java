package de.bp2019.zentraldatei.model;

import java.time.LocalDate;
import java.util.List;

public class ModuleSchemeTmp {
    private String name;
    private List<InstituteTmp> institutes;
    private LocalDate startDate;
    private LocalDate finishDate;
    private List<ExcerciseSchemeTmp> excerciseSchemes;
    private String calculationRule;
    private List<UserTmp> hasAccess;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public List<ExcerciseSchemeTmp> getExcerciseSchemes() {
        return excerciseSchemes;
    }

    public void setExcerciseSchemes(List<ExcerciseSchemeTmp> excerciseSchemes) {
        this.excerciseSchemes = excerciseSchemes;
    }

    public String getCalculationRule() {
        return calculationRule;
    }

    public void setCalculationRule(String calculationRule) {
        this.calculationRule = calculationRule;
    }

    public List<UserTmp> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(List<UserTmp> hasAccess) {
        this.hasAccess = hasAccess;
    }

    public ModuleSchemeTmp() {
    }

    public List<InstituteTmp> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(List<InstituteTmp> institutes) {
        this.institutes = institutes;
    }

}