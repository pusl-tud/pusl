package de.bp2019.zentraldatei.model.exercise;

import java.time.Instant;

import com.mongodb.lang.NonNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.model.module.Module;

/**
 * A class to model a single grade entry
 * 
 * @author Leon Chemnitz
 */
@Document
public class Grade {
	@Id
	private ObjectId id;

	@NonNull
	@DBRef
	private Module module;

	@NonNull
	private Exercise exercise;

	@NonNull
	@Indexed(unique = false)
	private long matrNumber;

	@NonNull
	@DBRef
	private User gradedBy;

	@NonNull
	/** Grade is stored as a string to enable non-numeric entries */
	private String grade;

	@NonNull
	private Instant handIn;

	public Grade() {
	}

	public Grade(Module module, Exercise exercise, long matrNumber, String grade, Instant handIn) {
		this.module = module;
		this.exercise = exercise;
		this.matrNumber = matrNumber;
		this.grade = grade;
		this.handIn = handIn;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}

	public long getMatrNumber() {
		return matrNumber;
	}

	public void setMatrNumber(long matrNumber) {
		this.matrNumber = matrNumber;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Instant getHandIn() {
		return handIn;
	}

	public void setHandIn(Instant handIn) {
		this.handIn = handIn;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
}