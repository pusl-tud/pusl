package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import de.bp2019.zentraldatei.model.ModuleSchemeTmp;

@Route("moduleSchemes/new")
public class ModuleSchemeView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    public ModuleSchemeView() {
        FormLayout layoutWithBinder = new FormLayout();
        Binder<ModuleSchemeTmp> binder = new Binder<>();

        // The object that will be edited
        ModuleSchemeTmp moduleSchemeBeingEdited = new ModuleSchemeTmp();

        // Create the fields
        TextField name = new TextField();
        name.setValueChangeMode(ValueChangeMode.EAGER);

        DatePicker startDate = new DatePicker();
        DatePicker finishDate = new DatePicker();

        TextField calculationRule = new TextField();
        calculationRule.setValueChangeMode(ValueChangeMode.EAGER);

        NativeButton save = new NativeButton("Save");
        NativeButton reset = new NativeButton("Reset");

        layoutWithBinder.addFormItem(name, "Name");
        layoutWithBinder.addFormItem(calculationRule, "Berechnungsregel");

        // Button bar
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, reset);
        save.getStyle().set("marginRight", "10px");

        // First name and last name are required fields
        name.setRequiredIndicatorVisible(true);
        startDate.setRequiredIndicatorVisible(true);
        finishDate.setRequiredIndicatorVisible(true);

        binder.forField(firstName).withValidator(new StringLengthValidator("Please add the first name", 1, null))
                .bind(Contact::getFirstName, Contact::setFirstName);
        binder.forField(lastName).withValidator(new StringLengthValidator("Please add the last name", 1, null))
                .bind(Contact::getLastName, Contact::setLastName);

        // Birthdate and doNotCall don't need any special validators
        binder.bind(doNotCall, Contact::isDoNotCall, Contact::setDoNotCall);
        binder.bind(birthDate, Contact::getBirthDate, Contact::setBirthDate);

        // Click listeners for the buttons
        save.addClickListener(event -> {
            if (binder.writeBeanIfValid(contactBeingEdited)) {
                infoLabel.setText("Saved bean values: " + contactBeingEdited);
            } else {
                BinderValidationStatus<Contact> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct().collect(Collectors.joining(", "));
                infoLabel.setText("There are errors: " + errorText);
            }
        });
        reset.addClickListener(event -> {
            // clear fields by setting null
            binder.readBean(null);
            infoLabel.setText("");
            doNotCall.setValue(false);
        });
    }

}