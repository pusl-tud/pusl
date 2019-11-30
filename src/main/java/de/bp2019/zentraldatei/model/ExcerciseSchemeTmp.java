package de.bp2019.zentraldatei.model;

import java.time.LocalTime;
import java.util.List;

public class ExcerciseSchemeTmp {
    private String name;
    private boolean isNumeric;
    private List<String> tokens;
    private LocalTime startDate;
    private LocalTime finishDate;
    private List<UserTmp> hasAccess;

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

}
