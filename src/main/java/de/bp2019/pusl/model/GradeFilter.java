package de.bp2019.pusl.model;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A Filter used to query {@link Grade}s. Used in {@link GradeComposer}. Not a
 * Database Entity
 * 
 * @author Leon Chemnitz
 */
public class GradeFilter {
    private Lecture lecture;
    private Exercise exercise;
    private String matrNumber;
    private String grade;

    private Date startDate;
    private Date endDate;

    public GradeFilter() {
    }

    public GradeFilter(Grade grade) {
        this.lecture = grade.getLecture();
        this.exercise = grade.getExercise();
        this.matrNumber = grade.getMatrNumber();
        this.grade = grade.getValue();
    }

    public GradeFilter(GradeFilter o) {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}