package de.bp2019.zentraldatei.model.exercise;

import java.util.ArrayList;
import java.util.List;

import de.bp2019.zentraldatei.model.module.Module;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A class to model a instance of an exercise. Consists of a {@link ExerciseScheme},
 * a list of {@link Grade} and a list of {@link Handout}
 * 
 * @author Alex Spaeth
 */
@Document
public class ExerciseInstance {

	@Id
	private String id;
	@DBRef
	private ExerciseScheme scheme;
	@DBRef
	private Module module;
	private List<Grade> grades;
	private List<Handout> handouts;

	public ExerciseInstance(ExerciseScheme scheme, Module module) {
		this.scheme = scheme;
		this.module = module;
		this.grades = new ArrayList<>();
		this.handouts = new ArrayList<>();
	}

	public ExerciseInstance(ExerciseScheme scheme, List<Grade> grades, List<Handout> handouts) {
		this.scheme = scheme;
		this.grades = grades;
		this.handouts = handouts;
	}

	public ExerciseInstance() {
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

	public List<Handout> getHandouts() {
		return handouts;
	}

	public void setHandouts(List<Handout> handouts) {
		this.handouts = handouts;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

}