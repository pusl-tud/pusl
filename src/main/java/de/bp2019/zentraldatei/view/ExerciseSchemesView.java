package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.zentraldatei.model.*;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Luca Dinies
 *
**/

public class ExerciseSchemesView extends VerticalLayout {


    public ExerciseSchemesView() {

        // Test Datensatz

        List<User> users = Arrays.asList(
                new User("Luca", "Dinies", null, null, null, null),
                new User("Leon", "Chemnitz", null, null, null, null),
                new User("Alexander", "Späth", null, null, null, null),
                new User("Tomoki", "Tokuyama", null, null, null, null),
                new User("Fabio", "da Costa", null, null, null, null)
        );


        FormLayout form = new FormLayout();
        Binder<ExerciseScheme> binder = new Binder();

        ExerciseScheme exerciseSchemeBeingEdited = new ExerciseScheme();

        // Create the Fields
        TextField name = new TextField("Name", "Name der Übung");
        name.setValueChangeMode(ValueChangeMode.EAGER);
        form.add(name);

        //TODO: Token eingabe

        DatePicker startDate  = new DatePicker("Start-Datum");
        DatePicker endDate  = new DatePicker("End-Datum");
        form.add(startDate, endDate);

        Checkbox isNumeric = new Checkbox("Mit Note");
        form.add(isNumeric);

        MultiselectComboBox<User> hasAccess = new MultiselectComboBox<User>("Zugriff erlauben");
        hasAccess.setItems(users);
        hasAccess.setItemLabelGenerator(User::toString);
        form.add(hasAccess);

        Label infoLabel = new Label();

        Button save = new Button("Speichern");
        Button reset = new Button("Reset");

        // Button Bar
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(save, reset);
        save.getStyle().set("marginRight", "10px");
        form.add(buttons);

        // Binding and validation
        binder.forField(name).withValidator(new StringLengthValidator("Bitte Namen der Übung eingeben", 1, null))
                .bind(ExerciseScheme::getName, ExerciseScheme::setName);

        binder.forField(startDate).asRequired("Bitte Anfangsdatum eingeben")
                .bind(ExerciseScheme::getStartDate, ExerciseScheme::setStartDate);

        binder.forField(endDate).asRequired("Bitte Enddatum eingeben")
                .bind(ExerciseScheme::getFinishDate, ExerciseScheme::setFinishDate);

        binder.bind(isNumeric, ExerciseScheme::getIsNumeric, ExerciseScheme::setIsNumeric);

        binder.bind(hasAccess, ExerciseScheme::getHasAccess, ExerciseScheme::setHasAccess);

        // Click-Listeners
        save.addClickListener(event -> {
            if (binder.writeBeanIfValid(exerciseSchemeBeingEdited)) {
                infoLabel.setText("Saved bean values: " + exerciseSchemeBeingEdited);
            } else {
                BinderValidationStatus<ExerciseScheme> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses()
                        .stream().filter(BindingValidationStatus::isError)
                        .map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct()
                        .collect(Collectors.joining(", "));
                infoLabel.setText("There are errors: "+ errorText);
            }
        });

        reset.addClickListener(event -> {
            binder.readBean(null);
            infoLabel.setText("");
            isNumeric.setValue(false);
        });

    }

}
