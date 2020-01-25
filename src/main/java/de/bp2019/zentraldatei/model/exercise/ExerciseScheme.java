package de.bp2019.zentraldatei.model.exercise;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.User;

import java.util.Set;

/**
 * A class to model a exercise scheme. Consists of a name, a flag for numeric
 * grading, a list of possible tokens, a start date, a finish date and a list of
 * {@link User} allowed
 * 
 * @author Alex Späth
 */
@Document
public class ExerciseScheme {

	@Id
	private String id;
	private String name;
	private boolean flexHandin;
	private boolean isNumeric;
	private String defaultValue;
	private Set<Token> tokens;
	@DBRef
	private Set<Institute> institutes;
	@DBRef
	private Set<User> hasAccess;

	public ExerciseScheme() {
	}

	public ExerciseScheme(String name, boolean flexHandin, boolean isNumeric, String defaultValue,
			Set<Token> tokens, Set<Institute> institutes, Set<User> hasAccess) {
		this.name = name;
		this.flexHandin = flexHandin;
		this.isNumeric = isNumeric;
		this.defaultValue = defaultValue;
		this.tokens = tokens;
		this.institutes = institutes;
		this.hasAccess = hasAccess;
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

	public Set<Institute> getInstitutes() {
		return institutes;
	}

	public void setInstitutes(Set<Institute> institutes) {
		this.institutes = institutes;
	}

	public Set<Token> getTokens() {
		return tokens;
	}

	public void setTokens(Set<Token> tokens) {
		this.tokens = tokens;
	}

	public Set<User> getHasAccess() {
		return hasAccess;
	}

	public void setHasAccess(Set<User> hasAccess) {
		this.hasAccess = hasAccess;
	}

	public void setNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}

	public boolean isFlexHandin() {
		return flexHandin;
	}

	public void setFlexHandin(boolean flexHandin) {
		this.flexHandin = flexHandin;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "ExerciseScheme [defaultValue=" + defaultValue + ", flexHandin=" + flexHandin + ", hasAccess="
				+ hasAccess + ", id=" + id + ", institutes=" + institutes + ", isNumeric=" + isNumeric + ", name="
				+ name + ", tokens=" + tokens + "]";
	}

}