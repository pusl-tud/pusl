package de.bp2019.zentraldatei.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class ExcerciseScheme {
    @Id
    private String id;
    private String name;
    private boolean isNumeric;
    private List<String> tokens;
    private List<User> hasAccess;

    public ExcerciseScheme(String id, String name, boolean isNumeric, List<String> tokens, List<User> hasAccess) {
        this.id = id;
        this.name = name;
        this.isNumeric = isNumeric;
        this.tokens = tokens;
        this.hasAccess = hasAccess;
    }

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

    public List<User> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(List<User> hasAccess) {
        this.hasAccess = hasAccess;
    }

}
