package de.bp2019.pusl.model;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Model of a Performance
 * 
 * @author Leon Chemnitz, Luca Dinies
 */
public class Performance {
    private String matriculationNumber;
    private PerformanceScheme performanceScheme;
    private String grade;

    public String getMatriculationNumber() {
        return this.matriculationNumber;
    }

    public void setMatriculationNumber(String matriculationNumber) {
        this.matriculationNumber = matriculationNumber;
    }

    public PerformanceScheme getPerformanceScheme() {
        return this.performanceScheme;
    }

    public void setPerformanceScheme(PerformanceScheme performanceScheme) {
        this.performanceScheme = performanceScheme;
    }

    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Performance(String matriculationNumber, PerformanceScheme performanceScheme, String grade) {
        this.matriculationNumber = matriculationNumber;
        this.performanceScheme = performanceScheme;
        this.grade = grade;
    }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o,
				Arrays.asList());
	}
}