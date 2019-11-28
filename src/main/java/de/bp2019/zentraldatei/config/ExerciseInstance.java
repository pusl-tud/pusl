package de.bp2019.zentraldatei.config;

import java.util.ArrayList;

class ExerciseInstance{
	
	private ExerciseScheme scheme;
	private ArrayList<Grade> grades;
	
	public ExerciseInstance(ExerciseScheme scheme, ArrayList<Grade> grades) {
		this.scheme = scheme;
		this.grades = grades;
	}
	
	public ExerciseScheme getScheme() {
		return scheme;
	}
	
	public ArrayList<Grade> getGrades(){
		return grades;
	}

}