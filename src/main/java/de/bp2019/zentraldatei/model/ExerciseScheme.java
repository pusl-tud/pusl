package de.bp2019.zentraldatei.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * A class to model a exercise scheme. Consists of a name, a flag for numeric
 * grading, a list of possible tokens, a start date, a finish date and a list of
 * users allowed
 * 
 * @author Alex Späth
 */
@Document
public class ExerciseScheme {

	@Id
	private String id;
	private String name;
	private boolean isNumeric;
	private Set<String> institutes;
	private Set<String> tokens;
	/** Foreign key - User.id */
	private Set<String> hasAccess;

	public ExerciseScheme(String name, boolean isNumeric, Set<String> institutes, Set<String> tokens, Set<String> hasAccess) {
		this.name = name;
		this.isNumeric = isNumeric;
		this.institutes = institutes;
		this.tokens = tokens;
		this.hasAccess = hasAccess;
	}

	public ExerciseScheme(){}

	public ExerciseScheme(ExerciseScheme exerciseScheme) {
		this.id = new String(exerciseScheme.getId());
		this.name = new String(exerciseScheme.getName());
		this.isNumeric = exerciseScheme.getIsNumeric();

		this.tokens = new HashSet<String>();
		exerciseScheme.getTokens().forEach(token -> this.tokens.add(new String(token)));

		this.hasAccess = new HashSet<String>();
		exerciseScheme.getHasAccess().forEach(user -> this.hasAccess.add(new String(user)));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Set<String> getInstitutes() {
		return institutes;
	}

	public void setInstitutes(Set<String> institutes) {
		this.institutes = institutes;
	}

	public Set<String> getTokens() {
		return tokens;
	}

	public void setTokens(Set<String> tokens) {
		this.tokens = tokens;
	}

	public Set<String> getHasAccess() {
		return hasAccess;
	}

	public void getHasAccess(Set<String> hasAccess) {
		this.hasAccess = hasAccess;
	}

	public ExerciseScheme copy() {
		ExerciseScheme copy = new ExerciseScheme();
		copy.setId(id);
		copy.setName(name);
		copy.setIsNumeric(isNumeric);
		copy.setTokens(tokens);

		return copy;
	}
}