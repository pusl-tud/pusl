package de.bp2019.zentraldatei.model.exercise;

import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.annotation.Id;

/**
 * Encapsulates an token as a class for ease of handling
 *
 * @author Luca Dinies
 */
@Document
public class Token {

    private String name;
    private boolean assignableByHIWI;

    public Token() {
    }

    public Token(String name, boolean assignableByHIWI) {
        this.name = name;
        this.assignableByHIWI = assignableByHIWI;
    }

    public Token copy() {
        Token copy = new Token();
        copy.setName(name);
        copy.setAssignableByHIWI(assignableByHIWI);
        return copy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAssignableByHIWI() {
        return assignableByHIWI;
    }

    public void setAssignableByHIWI(boolean assignableByHIWI) {
        this.assignableByHIWI = assignableByHIWI;
    }
}