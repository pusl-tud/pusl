package de.bp2019.zentraldatei.model;

import java.util.List;

/**
 * A class to model a instance of an exercise. consists of a exerciseScheme and
 * a list of grades
 * 
 * @author Alex Späth
 */
public class ExerciseInstance {

	private ExerciseScheme scheme;
	private List<Grade> grades;

	public ExerciseInstance(ExerciseScheme scheme, List<Grade> grades) {
		this.scheme = scheme;
		this.grades = grades;
	}

	public ExerciseScheme getScheme() {
		return scheme;
	}

	public void setScheme(ExerciseScheme scheme) {
		this.scheme = scheme;
	}

	public List<Grade> getGrades() {
		return grades;
	}

	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}
}