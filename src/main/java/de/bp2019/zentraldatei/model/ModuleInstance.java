package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Date;

/**
 * Class to model an instance of a module. Consists of a moduleScheme and a list
 * of excerciseInstances
 * 
 * @author Alex Späth
 */
@Document
public class ModuleInstance {

	@Id
	private String id;
	private ModuleScheme scheme;
	private Date startDate;
	private Date finishDate;
	private List<ExerciseInstance> exercises;

	public ModuleInstance(ModuleScheme scheme, Date startDate, Date finishDate, List<ExerciseInstance> exercises) {
		this.scheme = scheme;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.exercises = exercises;
	}

	public String getId() {
		return id;
	}

	public ModuleScheme getScheme() {
		return scheme;
	}

	public void setScheme(ModuleScheme scheme) {
		this.scheme = scheme;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public List<ExerciseInstance> getExercises() {
		return exercises;
	}

	public void getExercises(List<ExerciseInstance> exercises) {
		this.exercises = exercises;
	}

}