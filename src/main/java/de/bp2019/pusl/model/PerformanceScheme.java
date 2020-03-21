package de.bp2019.pusl.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}