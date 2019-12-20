package de.bp2019.zentraldatei.model.exercise;

import java.time.LocalDateTime;

/**
 * Model of an Exercise Handout with a Deadline
 * 
 * @author Leon Chemnitz
 */
public class Handout {
    private LocalDateTime handoutDate;
    private LocalDateTime deadlineDate;
    private long matrNumber;

    public Handout(LocalDateTime handoutDate, LocalDateTime deadlineDate, long matrNumber) {
        this.handoutDate = handoutDate;
        this.deadlineDate = deadlineDate;
        this.matrNumber = matrNumber;
    }

    public LocalDateTime getHandoutDate() {
        return handoutDate;
    }

    public void setHandoutDate(LocalDateTime handoutDate) {
        this.handoutDate = handoutDate;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public long getMatrNumber() {
        return matrNumber;
    }

    public void setMatrNumber(long matrNumber) {
        this.matrNumber = matrNumber;
    }
}
