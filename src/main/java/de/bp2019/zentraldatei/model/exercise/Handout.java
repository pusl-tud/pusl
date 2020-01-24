package de.bp2019.zentraldatei.model.exercise;

import java.time.Instant;
import java.time.LocalDateTime;

import com.mongodb.lang.NonNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model of an Exercise Handout with a Deadline
 * 
 * @author Leon Chemnitz
 */
@Document
public class Handout {
    @Id
    private ObjectId id;

    @NonNull
    @Indexed(unique = false)
    private long matrNumber;
    
    @NonNull
	@DBRef
	private Module module;

	@NonNull
	private Exercise exercise;

    @NonNull
    private Instant handoutDate;
    
    @NonNull
    private Instant deadlineDate;

    public Handout() {
    }

    public Handout(long matrNumber, Module module, Exercise exercise, Instant handoutDate,
    Instant deadlineDate) {
        this.matrNumber = matrNumber;
        this.module = module;
        this.exercise = exercise;
        this.handoutDate = handoutDate;
        this.deadlineDate = deadlineDate;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getMatrNumber() {
        return matrNumber;
    }

    public void setMatrNumber(long matrNumber) {
        this.matrNumber = matrNumber;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Instant getHandoutDate() {
        return handoutDate;
    }

    public void setHandoutDate(Instant handoutDate) {
        this.handoutDate = handoutDate;
    }

    public Instant getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Instant deadlineDate) {
        this.deadlineDate = deadlineDate;
    }    
}
