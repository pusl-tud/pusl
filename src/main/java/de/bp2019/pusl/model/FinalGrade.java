package de.bp2019.pusl.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Luca Dinies
 */
@Document
public class FinalGrade {

    public String matrikelNumber;
    public Lecture lecture;
    public PerformanceScheme performanceScheme;
    public String finalGrade;


    public FinalGrade() {
    }

    public FinalGrade(String matrikelNumber, Lecture lecture, PerformanceScheme performanceScheme, String finalGrade) {
        this.matrikelNumber = matrikelNumber;
        this.lecture = lecture;
        this.performanceScheme = performanceScheme;
        this.finalGrade = finalGrade;
    }

    public String getMatrikelNumber() {
        return matrikelNumber;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public PerformanceScheme getPerformanceScheme() {
        return performanceScheme;
    }

    public String getFinalGrade() {
        return finalGrade;
    }

    public void setMatrikelNumber(String matrikelNumber) {
        this.matrikelNumber = matrikelNumber;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public void setPerformanceScheme(PerformanceScheme performanceScheme) {
        this.performanceScheme = performanceScheme;
    }

    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }
}
