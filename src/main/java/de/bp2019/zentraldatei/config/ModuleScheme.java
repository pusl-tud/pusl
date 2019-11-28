package de.bp2019.zentraldatei.config;

import java.util.ArrayList;
import java.util.Date;

class ModuleScheme{
	
	private String name;
	private ArrayList<Institute> institutes;
	private Date startDate;
	private Date finishDate;
	private ArrayList<ExerciseScheme> exerciseSchemes;
	private String calculationRules;
	private ArrayList<User> hasAccess;
	
	public ModuleScheme(String name, ArrayList<Institute> institutes, Date startDate, Date finishDate,
	ArrayList<ExerciseScheme> exerciseSchemes, String calculationRules, ArrayList<User> hasAccess) {
		this.name = name;
		this.institutes = institutes;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.exerciseSchemes = exerciseSchemes;
		this.calculationRules = calculationRules;
		this.hasAccess = hasAccess;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Institute> getInstitutes(){
		return institutes;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
	public Date getFinishDate(){
		return finishDate;
	}
	public ArrayList<ExerciseScheme> getExerciseSchemes(){
		return exerciseSchemes;
	}
	
	public String getCaculationRules(){
		return calculationRules;
	}
	
	public ArrayList<User> getHasAccess(){
		return hasAccess;
	}
	
}