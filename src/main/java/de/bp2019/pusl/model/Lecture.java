package de.bp2019.pusl.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model of a Lecture. Is a Database Entity
 * 
 * @author Leon Chemnitz
 */
@Document
public class Lecture {

    @Id
    private ObjectId id;

    private String name;

    private Set<Institute> institutes;

    private Set<ObjectId> hasAccess;

    private List<Exercise> exercises;

    private List<PerformanceScheme> performanceSchemes;

    private Instant lastModified;

    public Lecture() {
        this.lastModified = Instant.now();
		this.institutes = new HashSet<>();
		this.hasAccess = new HashSet<>();
		this.exercises = new ArrayList<>();
		this.performanceSchemes = new ArrayList<>();
    }

    public Lecture(String name, Set<Institute> institutes, Set<ObjectId> hasAccess, List<Exercise> exercises,
            List<PerformanceScheme> performanceSchemes) {
        this.name = name;
        this.institutes = institutes;
        this.exercises = exercises;
        this.hasAccess = hasAccess;
        this.performanceSchemes = performanceSchemes;
        this.lastModified = Instant.now();
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

    public Set<Institute> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(Set<Institute> institutes) {
        this.institutes = institutes;
    }

    public Set<ObjectId> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(Set<ObjectId> hasAccess) {
        this.hasAccess = hasAccess;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public List<PerformanceScheme> getPerformanceSchemes() {
        return performanceSchemes;
    }

    public void setPerformanceSchemes(List<PerformanceScheme> performanceSchemes) {
        this.performanceSchemes = performanceSchemes;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
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
				Arrays.asList("name", "institutes", "hasAccess", "exercises", "performanceSchemes", "lastModified"));
	}
}