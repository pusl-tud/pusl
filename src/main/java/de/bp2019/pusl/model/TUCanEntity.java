package de.bp2019.pusl.model;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class TUCanEntity {
    public String number;
    public String matrNumber;
    public String firstName;
    public String middleName;
    public String lastName;
    public String grade;

    /**
     * get CellProcessors for CSV Processing
     * 
     * @return the cell processors
     * @author Leon Chemnitz
     */
    public static CellProcessor[] getCSVProcessors() {
        return new CellProcessor[] { new Optional(), // number
                new NotNull(), // matrNumber
                new Optional(), // firstName
                new Optional(), // middleName
                new Optional(), // lastName
                new Optional() // grade
        };
    }

    /**
     * 
     * @return
     * @author Leon Chemnitz
     */
    public static String[] getMapping() {
        return new String[] { "number", "matrNumber", "firstName", "middleName", "lastName", "grade" };
    }

    public static String[] getHeaders() {
        return new String[] { "Nummer", "Matrikelnummer", "Vorname", "Mittelname", "Name", "Bewertung" };
    }

    public static TUCanEntity fromPerformance(Performance performance) {
        TUCanEntity entity = new TUCanEntity();

        entity.matrNumber = performance.getMatriculationNumber();
        entity.grade = performance.getGrade();

        return entity;
    }

    public static TUCanEntity fromGrade(Grade grade) {
        TUCanEntity entity = new TUCanEntity();

        entity.matrNumber = grade.getMatrNumber();
        entity.grade = grade.getValue();

        return entity;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMatrNumber() {
        return matrNumber;
    }

    public void setMatrNumber(String matrNumber) {
        this.matrNumber = matrNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "TUCanEntity [firstName=" + firstName + ", grade=" + grade + ", lastName=" + lastName + ", matrNumber="
                + matrNumber + ", middleName=" + middleName + ", number=" + number + "]";
    }
}
