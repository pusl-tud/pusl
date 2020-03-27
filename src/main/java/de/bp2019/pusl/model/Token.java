package de.bp2019.pusl.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Encapsulates an token as a class for ease of handling. Used in
 * {@link ExerciseScheme}
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

    public boolean getAssignableByHIWI() {
        return assignableByHIWI;
    }

    public void setAssignableByHIWI(boolean assignableByHIWI) {
        this.assignableByHIWI = assignableByHIWI;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }
}