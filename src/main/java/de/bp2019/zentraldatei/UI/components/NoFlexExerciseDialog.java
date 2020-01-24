package de.bp2019.zentraldatei.UI.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import de.bp2019.zentraldatei.model.exercise.ExerciseScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Creates a dialog window to start an exercise for one student.
 *
 * @author Luca Dinies
 */
public class NoFlexExerciseDialog {

    private static final long serialVersionUID = 254687622689916454L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenEditor.class);

    public NoFlexExerciseDialog(ExerciseSchemeService exerciseSchemeService) {

        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("5em", 1), new FormLayout.ResponsiveStep("5em", 2));
        form.setWidth("100%");
        form.getStyle().set("marginLeft", "1em");
        form.getStyle().set("marginTop", "-0.5em");

        DatePicker datePicker = new DatePicker();
        //datePicker.setLabel("Abgabe-Datum");
        form.add(datePicker);

        Select<ExerciseScheme> exerciseSchemesSelect = new Select<>();
        exerciseSchemesSelect.setItemLabelGenerator(ExerciseScheme::getName);
        List<ExerciseScheme> allExerciseSchemes = exerciseSchemeService.getAllExerciseSchemes();
        exerciseSchemesSelect.setItems(allExerciseSchemes);
        exerciseSchemesSelect.setValue(allExerciseSchemes.get(0));
        exerciseSchemesSelect.setWidth("15em");
        exerciseSchemesSelect.setLabel("Übung");
        form.add(exerciseSchemesSelect);

        TextField matrikelNum = new TextField();
        matrikelNum.setPlaceholder("Matrikel Nummer");
        matrikelNum.setLabel("Matrikel Nummer");
        form.add(matrikelNum);

        HorizontalLayout buttonBar = new HorizontalLayout();

        Button cancel = new Button();
        cancel.setText("Abbruch");
        buttonBar.add(cancel);
        cancel.addClickListener(event1 -> {
            dialog.close();
        });

        Button save = new Button();
        save.setText("Speichern");
        buttonBar.add(save);
        form.add(buttonBar);
        dialog.add(form);

        dialog.open();
    }

}