package de.bp2019.pusl.model;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A class to model a single grade entry
 * 
 * @author Leon Chemnitz
 */
@Document
public class Grade {
	@Id
	private ObjectId id;

	@DBRef
	private Lecture lecture;

	private Exercise exercise;

	@Indexed(unique = false)

	private long matrNumber;

	@DBRef
	private User gradedBy;

	/** Grade is stored as a string to enable non-numeric entries */
	private String grade;

	private Instant handIn;

	public Grade() {
	}

	public Grade(Lecture lecture, Exercise exercise, long matrNumber, String grade, Instant handIn) {
		this.lecture = lecture;
		this.exercise = exercise;
		this.matrNumber = matrNumber;
		this.grade = grade;
		this.handIn = handIn;
	}

	public Lecture getLecture() {
		return lecture;
	}

	public void setLecture(Lecture lecture) {
		this.lecture = lecture;
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