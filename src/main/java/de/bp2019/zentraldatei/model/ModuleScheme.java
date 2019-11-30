package de.bp2019.zentraldatei.model;


import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Date;

/* 
 * A class to model a module. Consists of a name, a list of responsible institutes, a star date, a finish date,
 * a list of exerciseSchemes a grade calcuation rule an a list of privileged users
 */
@Document
public class ModuleScheme{
	
	//the calculation rule is stored as a string and later parsed
	private String name;
	private List<Institute> institutes;
	private Date startDate;
	private Date finishDate;
	private List<ExerciseScheme> exerciseSchemes;
	private String calculationRules;
	private List<User> hasAccess;
	
	public ModuleScheme(String name, List<Institute> institutes, Date startDate, Date finishDate,
	List<ExerciseScheme> exerciseSchemes, String calculationRules, List<User> hasAccess) {
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
	
	public void setName(String name) {
		this.name = name;
	}

	
	public List<Institute> getInstitutes(){
		return institutes;
	}
	
	public void setInstitutes(List<Institute> institutes){
		this.institutes = institutes;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}
	
	public Date getFinishDate(){
		return finishDate;
	}
	
	public void setFinishDate(Date finishDate){
		this.finishDate = finishDate;
	}
	
	public List<ExerciseScheme> getExerciseSchemes(){
		return exerciseSchemes;
	}
	
	public void setExerciseSchemes(List<ExerciseScheme> exerciseSchemes){
		this.exerciseSchemes = exerciseSchemes;
	}

	
	public String getCaculationRules(){
		return calculationRules;
	}
	
	public void getCaculationRules(String calculationRules){
		this.calculationRules = calculationRules;
	}
	
	public List<User> getHasAccess(){
		return hasAccess;
	}
	
	public void setHasAccess(List<User> hasAccess){
		this.hasAccess = hasAccess;
	}
}