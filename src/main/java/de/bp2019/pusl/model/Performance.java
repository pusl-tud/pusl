package de.bp2019.pusl.model;

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((grade == null) ? 0 : grade.hashCode());
        result = prime * result + ((matriculationNumber == null) ? 0 : matriculationNumber.hashCode());
        result = prime * result + ((performanceScheme == null) ? 0 : performanceScheme.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Performance other = (Performance) obj;
        if (grade == null) {
            if (other.grade != null)
                return false;
        } else if (!grade.equals(other.grade))
            return false;
        if (matriculationNumber == null) {
            if (other.matriculationNumber != null)
                return false;
        } else if (!matriculationNumber.equals(other.matriculationNumber))
            return false;
        if (performanceScheme == null) {
            if (other.performanceScheme != null)
                return false;
        } else if (!performanceScheme.equals(other.performanceScheme))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Performance [grade=" + grade + ", matriculationNumber=" + matriculationNumber + "]";
    }

}