package de.bp2019.pusl.service.dataproviders;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;

public class GradeFilter {
    private Lecture lecture;
    private Exercise exercise;
    private String matrNumber;
    private String grade;

    private LocalDate startDate;
    private LocalDate endDate;

    public GradeFilter() {
    }

    public GradeFilter(Grade grade) {
        this.lecture = grade.getLecture();
        this.exercise = grade.getExercise();
        this.matrNumber = grade.getMatrNumber();
        this.grade = grade.getValue();
    }

    public GradeFilter(GradeFilter o){
        this.lecture = o.getLecture();
        this.exercise = o.getExercise();
        this.matrNumber = o.getMatrNumber();
        this.grade = o.getGrade();
        this.startDate = o.getStartDate();
        this.endDate = o.getEndDate();
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public String getMatrNumber() {
        return matrNumber;
    }

    public void setMatrNumber(String matrNumber) {
        this.matrNumber = matrNumber;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    

}