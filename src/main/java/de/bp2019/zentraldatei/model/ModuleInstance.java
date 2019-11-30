package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/*
 * Class to model an instance of a module. Consists of a moduleScheme and a list of excerciseInstances
 */

@Document
class ModuleInstance{
	
	private ModuleScheme scheme;
	private List<ExerciseInstance> exercises;
	
	public ModuleInstance(ModuleScheme scheme, List<ExerciseInstance> exercises) {
		this.scheme = scheme;
		this.exercises = exercises;
	}
	
	public ModuleScheme getScheme() {
		return scheme;
	}
	
	public void setScheme(ModuleScheme scheme) {
		this.scheme = scheme;
	}
	
	public List<ExerciseInstance> getExercises(){
		return exercises;
	}
	
	public void getExercises(List<ExerciseInstance> exercises){
		this.exercises = exercises;
	}

}