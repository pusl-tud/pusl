package de.bp2019.pusl.model;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
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

	public ExerciseScheme(String name, boolean flexHandin, boolean isNumeric, String defaultValue, Set<Token> tokens,
			Set<Institute> institutes, Set<User> hasAccess) {
		this.name = name;
		this.flexHandin = flexHandin;
		this.isNumeric = isNumeric;
		this.defaultValue = defaultValue;
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
		return ToStringBuilder.reflectionToString(this);
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