package de.bp2019.pusl.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.types.ObjectId;

/**
 * A class to model a instance of an exercise. Always an embedded Document in a
 * {@link Lecture} therefore not a direct Database Entity.
 * 
 * @author Leon Chemnitz
 */
public class Exercise {

	private ObjectId id;
	private String name;
	private ExerciseScheme scheme;
	private boolean assignableByHIWI;

	public Exercise(Exercise o) {
		if(o == null) return;
		
		this.id = o.getId();
		this.name = new String(o.getName());
		this.scheme = o.getScheme();
		this.assignableByHIWI = o.isAssignableByHIWI(); 
	}

	public Exercise(String name, ExerciseScheme scheme, boolean assignableByHIWI) {
		this.setId(ObjectId.get());
		this.name = name;
		this.scheme = scheme;
		this.assignableByHIWI = assignableByHIWI;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Exercise() {
	}

	public ExerciseScheme getScheme() {
		return scheme;
	}

	public void setScheme(ExerciseScheme scheme) {
		this.scheme = scheme;
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

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}


}