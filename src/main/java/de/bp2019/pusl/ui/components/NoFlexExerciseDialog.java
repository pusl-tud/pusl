package de.bp2019.pusl.ui.components;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * Creates a dialog window to start an exercise for one student.
 *
 * @author Luca Dinies
 */
public class NoFlexExerciseDialog {

    private ObjectId objectId;

    private Binder<Grade> binder;

    @Autowired
    public NoFlexExerciseDialog(LectureService lectureService, GradeService gradeService) {

        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        binder = new Binder<>();

        /* ########### Create the Fields ########### */

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("5em", 1), new FormLayout.ResponsiveStep("5em", 2));
        form.setWidth("100%");
        form.getStyle().set("marginLeft", "1em");
        form.getStyle().set("marginTop", "-0.5em");

        TextField matrikelNum = new TextField();
        matrikelNum.setPlaceholder("Matrikel Nummer");
        matrikelNum.setLabel("Matrikel Nummer");
        form.add(matrikelNum);

        Select<Lecture> lectureSelect = new Select<>();
        lectureSelect.setItemLabelGenerator(Lecture::getName);
        lectureSelect.setDataProvider(lectureService);
        lectureSelect.setPlaceholder("Modul");
        lectureSelect.setLabel("Modul");
        form.add(lectureSelect);

        DatePicker datePicker = new DatePicker();
        datePicker.setLabel("Abgabe-Datum");
        datePicker.setValue(LocalDate.now());
        datePicker.setVisible(true);
        form.add(datePicker);

        Select<Exercise> exerciseSelect = new Select<>();
        exerciseSelect.setItemLabelGenerator(Exercise::getName);
        exerciseSelect.setEnabled(false);
        exerciseSelect.setLabel("Übung");

        form.add(exerciseSelect);

        Select<String> select = new Select<>();
        select.setItems("hallo", "test");
        select.setEmptySelectionCaption("Alle Anzeigen");
        select.setEmptySelectionAllowed(true);
        select.addComponents(null, new Hr());
        form.add(select);

        TextField gradeField = new TextField();
        gradeField.setLabel("Note");
        gradeField.setPlaceholder("Note");
        form.add(gradeField);

        Select<Token> tokenSelect = new Select<>();
        tokenSelect.setItemLabelGenerator(Token::getName);
        tokenSelect.setEnabled(false);
        tokenSelect.setLabel("Token");
        form.add(tokenSelect);

        /* ########### Change Listeners for Selects ########### */

        lectureSelect.addValueChangeListener(event -> {
            Lecture selectedLecture = lectureSelect.getValue();
            List<Exercise> exercises = selectedLecture.getExercises();
            exerciseSelect.setItems(exercises);
            exerciseSelect.setEnabled(true);
            exerciseSelect.setValue(exercises.get(0));
        });

        exerciseSelect.addValueChangeListener(event -> {
            if (!exerciseSelect.isEmpty()) {
                Exercise selectedExercise = exerciseSelect.getValue();
                Set<Token> token = selectedExercise.getScheme().getTokens();
                tokenSelect.setItems(token);
                tokenSelect.setEnabled(true);

                if (selectedExercise.getScheme().getIsNumeric()) {
                    tokenSelect.setEnabled(false);
                    gradeField.setEnabled(true);
                } else {
                    tokenSelect.setEnabled(true);
                    gradeField.setEnabled(false);
                }
            }
        });

        /* ########### Button Bar ########### */

        HorizontalLayout buttonBar = new HorizontalLayout();

        Button cancel = new Button();
        cancel.setText("Abbruch");
        buttonBar.add(cancel);

        Button save = new Button();
        save.setText("Speichern");
        buttonBar.add(save);
        form.add(buttonBar);
        dialog.add(form);
        dialog.open();

        /* ########### Data Binding and validation ########### */

        // TODO: Validator
        binder.forField(matrikelNum).withValidator(new StringLengthValidator("Bitte Matrikelnummer eingeben", 1, null))
                .bind(Grade::getMatrNumber, Grade::setMatrNumber);

        binder.bind(lectureSelect, Grade::getLecture, Grade::setLecture);

        binder.bind(exerciseSelect, Grade::getExercise, Grade::setExercise);

        binder.bind(gradeField, Grade::getValue, Grade::setValue);

        binder.bind(datePicker, Grade::getHandIn, Grade::setHandIn);

        /* ########### Click Listeners for Buttons ########### */

        cancel.addClickListener(event1 -> {
            dialog.close();
        });

        save.addClickListener(event -> {
            Grade grade = new Grade();
            if (binder.writeBeanIfValid(grade)) {
                if (objectId != null) {
                    grade.setId(objectId);
                }
                try {
                    gradeService.save(grade);
                    dialog.close();
                } catch (UnauthorizedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    // TODO: implement ErrorHandling
                }
            }
        });

    }

}