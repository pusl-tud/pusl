package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;



/*
 * A class to model a instance of an exercise. consists of a exerciseScheme and a list of grades
 */


public class ExerciseInstance{
	
	private ExerciseScheme scheme;
	private ArrayList<Grade> grades;
	
	public ExerciseInstance(ExerciseScheme scheme, ArrayList<Grade> grades) {
		this.scheme = scheme;
		this.grades = grades;
	}
	
	public ExerciseScheme getScheme(ExerciseScheme scheme) {
		return scheme;
	}
	
	public void setScheme() {
		this.scheme = scheme;
	}
	
	
	public ArrayList<Grade> getGrades(){
		return grades;
	}

	public void setGrades(ArrayList<Grade> grades){
		this.grades = grades;
	}
}