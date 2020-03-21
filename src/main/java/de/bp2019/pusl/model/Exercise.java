package de.bp2019.pusl.model;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A class to model a instance of an exercise. Always an embedded Document in a
 * {@link Lecture}.
 * 
 * @author Leon Chemnitz
 */
public class Exercise {

	private String name;
	
	private ExerciseScheme scheme;

	private boolean assignableByHIWI;

	public Exercise(String name, ExerciseScheme scheme, boolean assignableByHIWI) {
		this.name = name;
		this.scheme = scheme;
		this.assignableByHIWI = assignableByHIWI;
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
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, Arrays.asList("scheme", "assignableByHIWI"));
	}

}