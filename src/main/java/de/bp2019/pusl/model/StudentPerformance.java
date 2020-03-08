package de.bp2019.pusl.model;

import java.util.HashMap;
import java.util.Map;

public class StudentPerformance {
    private String matriculationNumber;
    private Lecture lecture;
    private Map<PerformanceScheme, String> performances;

    public StudentPerformance(String matriculationNumber, Lecture lecture) {
        this.matriculationNumber = matriculationNumber;
        this.lecture = lecture;
        performances = new HashMap<>();
    }

    public void setPerformance(PerformanceScheme performanceScheme, String result) {
        performances.put(performanceScheme, result);
    }

    public String getPerformance(PerformanceScheme performanceScheme){
        return performances.get(performanceScheme);
    }

    public String getMatriculationNumber() {
        return matriculationNumber;
    }

    public void setMatriculationNumber(String matriculationNumber) {
        this.matriculationNumber = matriculationNumber;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }
}