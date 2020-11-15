package de.bp2019.pusl.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model of a grade entry. Is a Database Entity.
 * 
 * @author Leon Chemnitz
 */
@Document
@CompoundIndexes({
    @CompoundIndex(name = "grade_lecture", def = "{'lecture._id' : 1}"),
    @CompoundIndex(name = "lecture_exercise", def = "{'lecture._id' : 1, 'exercise.name': 1}"),
    @CompoundIndex(name = "matr_lecture", def = "{'matrNumber' : 1, 'lecture._id': 1}"),
    @CompoundIndex(name = "matr_lecture_exercise_handIn", def = "{'matrNumber' : 1, 'lecture._id': 1, 'exercise.name': 1, 'handIn': 1}"),
})
public class Grade {
	@Id
	private ObjectId id;

	private Lecture lecture;

	private Exercise exercise;

	@Indexed(name= "grade_matr", unique = false)
	private String matrNumber;

	// TODO: Durch ObjectID ersetzen
	private User gradedBy;

	private String value;
	
	private LocalDate handIn;

	private LocalDateTime lastModified;

	public Grade() {
		lastModified = LocalDateTime.now();
	}

	public Grade(Lecture lecture, Exercise exercise, String matrNumber, String value, LocalDate handIn, User gradedBy) {
		this.lecture = lecture;
		this.exercise = exercise;
		this.matrNumber = matrNumber;
		this.value = value;
		this.handIn = handIn;
		this.gradedBy = gradedBy;

		lastModified = LocalDateTime.now();
	}

	public void setLecture(Lecture lecture){
		this.lecture = lecture;
	}

	public Lecture getLecture(){
		return lecture;
	}
	
	public String getMatrNumber() {
		return matrNumber;
	}

	public void setMatrNumber(String matrNumber) {
		this.matrNumber = matrNumber;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String grade) {
		this.value = grade;
	}

	public LocalDate getHandIn() {
		return handIn;
	}

	public void setHandIn(LocalDate handIn) {
		this.handIn = handIn;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public User getGradedBy() {
		return gradedBy;
	}

	public void setGradedBy(User gradedBy) {
		this.gradedBy = gradedBy;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o,
				Arrays.asList("lecture", "lectureEmb", "exercise", "matrNumber", "gradedBy", "grade", "handIn", "lastModified"));
	}

	@JsonIgnore
	public static String getNameOfGradedBy(Grade grade) {
		if(grade.getGradedBy() != null && grade.getGradedBy().getFullName() != null){
			return grade.getGradedBy().getFullName();
		}

		return "nich gesetzt";
	}

	@JsonIgnore
	public static String getLastModifiedFormatted(Grade grade) {
		if(grade.getLastModified() != null){
			return grade.getLastModified().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}

		return "nich gesetzt";
	}	
}