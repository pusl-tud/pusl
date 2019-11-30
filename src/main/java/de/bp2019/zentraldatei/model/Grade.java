package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
/*
 * A class to model a single grade entry. Consists of a matriculation number, a grade and a hand in date
 */
@Document
class Grade{
	
	//Grade is stored as a string to enable non-numeric entries
	private int matrNumber;
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
	
	public void setMatrNumber(int martNumber) {
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