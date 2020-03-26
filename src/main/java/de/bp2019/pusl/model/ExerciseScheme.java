package de.bp2019.pusl.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model of an ExerciseScheme. Used in {@link Exercise}
 * 
 * @author Leon Chemnitz
 */
@Document
public class ExerciseScheme {

	@Id
	private ObjectId id;
	private String name;
	private boolean isNumeric;
	private double defaultValueNumeric;
	private Token defaultValueToken;
	private Set<Token> tokens;
	private Set<Institute> institutes;
	private Set<User> hasAccess;

	public ExerciseScheme() {
		this.defaultValueNumeric = 5.0;
		this.tokens = new HashSet<>();
		this.institutes = new HashSet<>();
		this.hasAccess = new HashSet<>();
	}

	public ExerciseScheme(String name, boolean isNumeric, double defaultValueNumeric, Token defaultValueToken, Set<Token> tokens,
			Set<Institute> institutes, Set<User> hasAccess) {
		this.name = name;
		this.isNumeric = isNumeric;
		this.defaultValueNumeric = defaultValueNumeric;
		this.tokens = tokens;
		this.institutes = institutes;
		this.hasAccess = hasAccess;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNumeric() {
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

	public double getDefaultValueNumeric() {
		return defaultValueNumeric;
	}

	public void setDefaultValueNumeric(double defaultValueNumeric) {
		this.defaultValueNumeric = defaultValueNumeric;
	}

	public Token getDefaultValueToken() {
		return defaultValueToken;
	}

	public void setDefaultValueToken(Token defaultValueToken) {
		this.defaultValueToken = defaultValueToken;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);

	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o,
				Arrays.asList("name", "flexHandin", "isNumeric", "defaultValue", "tokens", "institutes", "hasAccess"));
	}

}