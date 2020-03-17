package de.bp2019.pusl.model;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Model of a PerformanceScheme used in {@link Lecture}
 * 
 * @author Leon Chemnitz
 */
public class PerformanceScheme {
    private String name;
    private String calculationRule;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalculationRule() {
        return calculationRule;
    }

    public void setCalculationRule(String calculationRule) {
        this.calculationRule = calculationRule;
    }

    public PerformanceScheme(String name, String calculationRule) {
        this.name = name;
        this.calculationRule = calculationRule;
    }

    public PerformanceScheme() {
    }

    
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o,
				Arrays.asList("calculationRule"));
	}
}