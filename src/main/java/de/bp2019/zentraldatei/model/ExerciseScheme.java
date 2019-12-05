package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * A class to model a exercise scheme. Connsists of a name, a flag for numeric
 * grading, a list of possible tokens, a start date, a finish date and a list of
 * users alowed
 * 
 * @author Alex Späth
 */
@Document
public class ExerciseScheme {

	@Id
	private String id;
	private String name;
	private boolean isNumeric;
	private List<String> tokens;
	private List<User> hasAccess;

	public ExerciseScheme(String id, String name, boolean isNumeric, List<String> tokens, List<User> hasAccess) {
		this.id = id;
		this.name = name;
		this.isNumeric = isNumeric;
		this.tokens = tokens;
		this.hasAccess = hasAccess;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsNumeric() {
		return isNumeric;
	}

	public void setIsNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	public List<User> getHasAccess() {
		return hasAccess;
	}

	public void getHasAccess(List<User> hasAccess) {
		this.hasAccess = hasAccess;
	}

}