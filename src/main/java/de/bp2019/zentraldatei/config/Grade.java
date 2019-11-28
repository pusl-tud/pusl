package de.bp2019.zentraldatei.config;

import java.util.Date;

class Grade{
	
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

	public String getGrade() {
		return grade;
	}
	
	public Date getHandIn() {
		return handIn;
	}
}