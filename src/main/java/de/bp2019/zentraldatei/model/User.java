package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


/* A class to model a useser. Consists of a  first and last name, an E-mail adress, a password,
 *a list of institutes the user belongs to, and the user type
 */

@Document
class User{
	
	private String firstName;
	private String lastName;
	private String eMail;
	private String password;
	private List<Institute> institutes;
	private UserType type;
	
	public User(String firstName, String lastName, String eMail, String password, List<Institute> institutes, UserType type) {
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
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEMail() {
		return eMail;
	}
	
	public void setEMail(String eMail) {
		this.eMail = eMail;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<Institute> getInstitutes(){
		return institutes;
	}
	
	public void setInstitutes(List<Institute> institutes){
		this.institutes = institutes;
	}
	
	public UserType getType() {
		return type;
	}
	
	public void setType(UserType type) {
		this.type = type;
	}

}