package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Date;
import java.util.Set;

/* A class to model a exercise scheme. Connsists of a name, a flag for numeric grading, a list of possible tokens,
 * a start date, a finish date and a list of users alowed
 */

@Document

public class ExerciseScheme{

    private String name;
    private boolean isNumeric;
    private List<String> tokens;
    private LocalDate startDate;
    private LocalDate finishDate;
    private Set<User> hasAccess;

    public ExerciseScheme(String name, boolean isNumeric, List<String> tokens, LocalDate startDate, LocalDate finishDate, Set<User> hasAccess) {
        this.name = name;
        this.isNumeric = isNumeric;
        this.tokens = tokens;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.hasAccess = hasAccess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsNumeric() {
        return isNumeric;
    }

    public void setIsNumeric(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }

    public List<String> getTokens(){
        return tokens;
    }

    public void setTokens(List<String> tokens){
        this.tokens = tokens;
    }

    public LocalDate getStartDate(){
        return startDate;
    }

    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }

    public LocalDate getFinishDate(){
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate){
        this.finishDate = finishDate;
    }

    public Set<User> getHasAccess(){
        return hasAccess;
    }

    public void setHasAccess(Set<User> hasAccess){
        this.hasAccess = hasAccess;
    }

    public ExerciseScheme() {

    }

}