package de.bp2019.pusl.model;

import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * A class to model a instance of an exercise. Always an embedded Document in a
 * {@link Lecture}.
 * 
 * @author Leon Chemnitz
 */
public class Exercise {

	private String name;
	
	@DBRef
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Exercise other = (Exercise) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}