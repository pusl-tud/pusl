package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;

/*
 * An class to model the diffent user types
 */
@Document
class UserType {

	private String type;
	
	public UserType (String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}