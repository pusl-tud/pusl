package de.bp2019.zentraldatei.model.exercise;

import java.time.LocalDateTime;

/**
 * A class to model a single grade entry. Consists of a matriculation number, a
 * grade and a hand in date
 * 
 * @author Alex Späth
 */
public class Grade {
	private long matrNumber;
	/** Grade is stored as a string to enable non-numeric entries */
	private String grade;
	private LocalDateTime handIn;

	public Grade(long matrNumber, String grade, LocalDateTime handIn) {
		this.matrNumber = matrNumber;
		this.grade = grade;
		this.handIn = handIn;
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

	public LocalDateTime getHandIn() {
		return handIn;
	}

	public void setHandIn(LocalDateTime handIn) {
		this.handIn = handIn;
	}
}