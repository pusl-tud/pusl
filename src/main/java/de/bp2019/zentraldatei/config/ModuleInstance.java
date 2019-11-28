package de.bp2019.zentraldatei.config;

import java.util.ArrayList;

class ModuleInstance{
	
	private ModuleScheme scheme;
	private ArrayList<ExerciseInstance> exercises;
	
	public ModuleInstance(ModuleScheme scheme, ArrayList<ExerciseInstance> exercises) {
		this.scheme = scheme;
		this.exercises = exercises;
	}
	
	public ModuleScheme getScheme() {
		return scheme;
	}
	
	public ArrayList<ExerciseInstance> getExercises(){
		return exercises;
	}

}