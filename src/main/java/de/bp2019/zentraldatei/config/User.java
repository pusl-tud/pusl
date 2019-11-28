package de.bp2019.zentraldatei.config;

import java.util.ArrayList;

class User{
	
	private String firstName;
	private String lastName;
	private String eMail;
	private String password;
	private ArrayList<Institute> institutes;
	private UserType type;
	
	public User(String firstName, String lastName, String eMail, String password, ArrayList<Institute> institutes, UserType type) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.eMail = eMail;
		this.password = password;
		this.institutes = institutes;
		this.type = type;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getEMail() {
		return eMail;
	}
	
	public ArrayList<Institute> getInstitutes(){
		return institutes;
	}
	
	public UserType getType() {
		return type;
	}

}