package de.bp2019.zentraldatei.model;

import java.util.Date;

/**
 * A class to model a single grade entry. Consists of a matriculation number, a
 * grade and a hand in date
 * 
 * @author Alex Späth
 */
public class Grade {
	private int matrNumber;
	/** Grade is stored as a string to enable non-numeric entries */
	private String grade;
	private Date handIn;

	public Grade(int matrNumber, String grade, Date handIn) {
		this.matrNumber = matrNumber;
		this.grade = grade;
		this.handIn = handIn;
	}

	public int getMatrNumber() {
		return matrNumber;
	}

	public void setMatrNumber(int matrNumber) {
		this.matrNumber = matrNumber;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Date getHandIn() {
		return handIn;
	}

	public void setHandIn(Date handIn) {
		this.handIn = handIn;
	}
}