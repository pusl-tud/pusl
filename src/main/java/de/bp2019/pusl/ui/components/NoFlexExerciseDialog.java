package de.bp2019.pusl.ui.components;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;

/**
 * Creates a dialog window to start an exercise for one student.
 *
 * @author Luca Dinies
 */
public class NoFlexExerciseDialog {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenEditor.class);

    private ObjectId objectId;

    Binder<Grade> binder;

    @Autowired
    public NoFlexExerciseDialog(LectureService lectureService, ExerciseSchemeService exerciseSchemeService,
            GradeService gradeService) {

        LOGGER.debug("started creation of NoFlexExerciseDialog");

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
        lectureSelect.setItems(lectureService.getAll());
        lectureSelect.setPlaceholder("Veranstaltung");
        lectureSelect.setLabel("Veranstaltung");
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
                .withConverter(new StringToLongConverter("Bitte eine Zahl eingeben!"))
                .bind(Grade::getMatrNumber, Grade::setMatrNumber);

        binder.bind(lectureSelect, Grade::getLecture, Grade::setLecture);

        binder.bind(exerciseSelect, Grade::getExercise, Grade::setExercise);

        binder.bind(gradeField, Grade::getGrade, Grade::setGrade);

        // binder.bind(datePicker, grade -> LocalDate.now(), grade -> {});

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
                } finally {
                    // TODO: implement ErrorHandling
                }
            }
        });


        
        LOGGER.debug("finished creation of NoFlexExerciseDialog");
    }

}