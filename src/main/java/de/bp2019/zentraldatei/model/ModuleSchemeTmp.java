package de.bp2019.zentraldatei.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import de.bp2019.zentraldatei.view.components.DefaultFactory;
import de.bp2019.zentraldatei.view.components.ICustomListItem;

public class ModuleSchemeTmp implements ICustomListItem{
    private String name;
    private Set<InstituteTmp> institutes;
    private LocalDate startDate;
    private LocalDate finishDate;
    private List<ExcerciseSchemeTmp> excerciseSchemes;
    private String calculationRule;
    private Set<UserTmp> hasAccess;

    @Override
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

    public Set<UserTmp> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(Set<UserTmp> hasAccess) {
        this.hasAccess = hasAccess;
    }

    public ModuleSchemeTmp() {
    }

    public Set<InstituteTmp> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(Set<InstituteTmp> institutes) {
        this.institutes = institutes;
    }

    @Override
    public String toString() {
        return "ModuleSchemeTmp [calculationRule=" + calculationRule + ", excerciseSchemes=" + excerciseSchemes
                + ", finishDate=" + finishDate + ", hasAccess=" + hasAccess + ", institutes=" + institutes + ", name="
                + name + ", startDate=" + startDate + "]";
    }

    public static class ModuleSchemeDefaultFactory implements DefaultFactory<ModuleSchemeTmp> {

        @Override
        public ModuleSchemeTmp createDefaultInstance() {
            return new ModuleSchemeTmp("Neue Veranstaltung", null, LocalDate.now(), LocalDate.now(), null, null, null);
        }
 
    }

    public ModuleSchemeTmp(String name, Set<InstituteTmp> institutes, LocalDate startDate, LocalDate finishDate,
            List<ExcerciseSchemeTmp> excerciseSchemes, String calculationRule, Set<UserTmp> hasAccess) {
        this.name = name;
        this.institutes = institutes;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.excerciseSchemes = excerciseSchemes;
        this.calculationRule = calculationRule;
        this.hasAccess = hasAccess;
    }
}