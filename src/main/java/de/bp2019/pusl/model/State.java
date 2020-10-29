package de.bp2019.pusl.model;

import java.util.List;

public class State {
    private List<Lecture> lectures;
    private List<Grade> grades;
    private List<Institute> institutes;
    private List<User> users;
    private List<ExerciseScheme> exerciseSchemes;

    public List<ExerciseScheme> getExerciseSchemes() {
        return exerciseSchemes;
    }

    public List<Lecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    public List<Grade> getGrades() {
		return grades;
	}

	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}

	public List<Institute> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(List<Institute> institutes) {
        this.institutes = institutes;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setExerciseSchemes(List<ExerciseScheme> exerciseSchemes) {
        this.exerciseSchemes = exerciseSchemes;
    }

    @Override
    public String toString() {
        return "State [exerciseSchemes=" + exerciseSchemes + ", grades=" + grades + ", institutes=" + institutes
                + ", lectures=" + lectures + ", users=" + users + "]";
    }
}
