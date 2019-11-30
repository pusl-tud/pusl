package de.bp2019.zentraldatei.model;

import java.time.LocalTime;
import java.util.List;

import de.bp2019.zentraldatei.view.components.DefaultFactory;
import de.bp2019.zentraldatei.view.components.ICustomListItem;


public class ExcerciseSchemeTmp implements ICustomListItem{
    private String name;
    private boolean isNumeric;
    private List<String> tokens;
    private LocalTime startDate;
    private LocalTime finishDate;
    private List<UserTmp> hasAccess;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    public void setNumeric(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public LocalTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalTime startDate) {
        this.startDate = startDate;
    }

    public LocalTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalTime finishDate) {
        this.finishDate = finishDate;
    }

    public List<UserTmp> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(List<UserTmp> hasAccess) {
        this.hasAccess = hasAccess;
    }

    public ExcerciseSchemeTmp() {
    }

    public ExcerciseSchemeTmp(String name, boolean isNumeric, List<String> tokens, LocalTime startDate,
            LocalTime finishDate, List<UserTmp> hasAccess) {
        this.name = name;
        this.isNumeric = isNumeric;
        this.tokens = tokens;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.hasAccess = hasAccess;
    }

    public static class ExcerciseSchemeDefaultFactory implements DefaultFactory<ExcerciseSchemeTmp> {

        @Override
        public ExcerciseSchemeTmp createDefaultInstance() {
            return new ExcerciseSchemeTmp("Neues Prüfungsschema", true, null, LocalTime.now(), null, null);
        }
 
    }

}
