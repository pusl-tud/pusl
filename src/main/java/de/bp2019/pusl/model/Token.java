package de.bp2019.pusl.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Encapsulates an token as a class for ease of handling. Used in
 * {@link ExerciseScheme}
 *
 * @author Luca Dinies
 */
@Document
public class Token {

    private ObjectId id;
    private String name;
    private boolean assignableByHIWI;

    public Token() {
    }

    public Token(Token o){
        this.id = o.getId();
        this.name = o.getName();
        this.assignableByHIWI = o.getAssignableByHIWI();
    }

    public Token(String name, boolean assignableByHIWI) {
        setId(ObjectId.get());
        this.name = name;
        this.assignableByHIWI = assignableByHIWI;
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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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