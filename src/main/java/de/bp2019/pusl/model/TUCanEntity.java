package de.bp2019.pusl.model;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class TUCanEntity {
    private String number;
    private String matrNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String grade;

    /**
     * Sets up the processors used for the examples. There are 10 CSV columns, so 10
     * processors are defined. Empty columns are read as null (hence the NotNull()
     * for mandatory columns).
     * 
     * @return the cell processors
     */
    public static CellProcessor[] getCSVProcessors() {
        final CellProcessor[] processors = new CellProcessor[] {
                new Optional(), // number
                new NotNull(), // matrNumber
                new Optional(), // firstName
                new Optional(), // middleName
                new Optional(), // lastName
                new NotNull() // grade
        };

        return processors;
    }

    public static String[] getMapping() {
        return new String[] { "number", "matrNumber", "firstName", "middleName", "lastName", "grade"};
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
