package de.bp2019.zentraldatei.view;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.zentraldatei.model.ExcerciseSchemeTmp;
import de.bp2019.zentraldatei.model.InstituteTmp;
import de.bp2019.zentraldatei.model.ModuleSchemeTmp;
import de.bp2019.zentraldatei.model.UserTmp;
import de.bp2019.zentraldatei.view.components.CustomListComponent;

/**
 * View containing a form to edit a ModuleScheme
 * 
 * @author Leon Chemnitz
 */
@Route("moduleSchemes/new")
public class ModuleSchemeView extends Div {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleSchemeView.class);

    public ModuleSchemeView() {
        /* Platzhalter Code da noch keine echten Repositories existieren */
        List<InstituteTmp> allInstitutes = Arrays.asList(
                new InstituteTmp("Bahntechnik"),
                new InstituteTmp("Straßenwesen"),
                new InstituteTmp("Computergrafik"));

        List<UserTmp> allUsers = Arrays.asList(
                new UserTmp("Walter", "Frosch", null, null, null, null),
                new UserTmp("Peter", "Pan", null, null, null, null),
                new UserTmp("Angela", "Merkel", null, null, null, null),
                new UserTmp("John", "Lennon", null, null, null, null),
                new UserTmp("Helene", "Fischer", null, null, null, null),
                new UserTmp("Walter", "Gropius", null, null, null, null));
        /* Ab Hier echter Code */

        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("10em", 2));
        layoutWithBinder.setWidth("35em");

        /* Binder to bind the form Data to an Object */
        Binder<ModuleSchemeTmp> binder = new Binder<>();

        /* The object that will be edited */
        ModuleSchemeTmp moduleSchemeBeingEdited = new ModuleSchemeTmp();

        /* -- Create the fields -- */
        TextField name = new TextField();
        name.setLabel("Name");
        name.setPlaceholder("Name Der Veranstaltung");
        name.setValueChangeMode(ValueChangeMode.EAGER);
        layoutWithBinder.add(name);

        MultiselectComboBox<InstituteTmp> institutes = new MultiselectComboBox<InstituteTmp>();
        institutes.setLabel("Institute");
        institutes.setItems(allInstitutes);
        institutes.setItemLabelGenerator(InstituteTmp::getName);
        layoutWithBinder.add(institutes, 2);

        DatePicker startDate = new DatePicker();
        startDate.setValue(LocalDate.now());
        startDate.setLabel("Beginnt am");
        layoutWithBinder.add(startDate);

        DatePicker finishDate = new DatePicker();
        finishDate.setValue(LocalDate.now());
        finishDate.setLabel("Endet am");
        layoutWithBinder.add(finishDate);

        MultiselectComboBox<UserTmp> hasAccess = new MultiselectComboBox<UserTmp>();
        hasAccess.setLabel("Hat Zugriff");
        hasAccess.setItems(allUsers);
        hasAccess.setItemLabelGenerator(UserTmp::getFullName);
        layoutWithBinder.add(hasAccess, 2);

        CustomListComponent<ExcerciseSchemeTmp> excerciseSchemes = new CustomListComponent<ExcerciseSchemeTmp>(new ExcerciseSchemeTmp.ExcerciseSchemeDefaultFactory());
        excerciseSchemes.setLabel("Prüfungsschemas");
        excerciseSchemes.setButtonText("Prüfungsschema hinzufügen");
        layoutWithBinder.add(excerciseSchemes, 2);

        TextArea calculationRule = new TextArea();
        calculationRule.setValueChangeMode(ValueChangeMode.EAGER);
        calculationRule.setLabel("Berechnungsregel");
        calculationRule.setPlaceholder("Platzhalter");
        layoutWithBinder.add(calculationRule, 2);

        Button save = new Button("Save");
        Button reset = new Button("Reset");

        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, reset);
        save.getStyle().set("marginRight", "10px");
        layoutWithBinder.add(actions, 2);

        /* -- Data Binding and validation -- */
        binder.forField(name).withValidator(new StringLengthValidator("Bitte Name der Veranstaltung angeben", 1, null))
                .bind(ModuleSchemeTmp::getName, ModuleSchemeTmp::setName);

        binder.forField(institutes)
                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(), "Bitte mind. ein Institut angeben")
                .bind(ModuleSchemeTmp::getInstitutes, ModuleSchemeTmp::setInstitutes);

        binder.forField(startDate).asRequired("Bitte Anfang der Veranstaltung angeben")
                .bind(ModuleSchemeTmp::getStartDate, ModuleSchemeTmp::setStartDate);

        binder.forField(finishDate).asRequired("Bitte Ende der Veranstaltung angeben")
                .withValidator(selectedDate -> selectedDate.compareTo(startDate.getValue()) >= 0,
                        "Ende der Veranstaltung muss nach dem Beginn der Veranstaltung liegen")
                .bind(ModuleSchemeTmp::getFinishDate, ModuleSchemeTmp::setFinishDate);

        binder.bind(hasAccess, ModuleSchemeTmp::getHasAccess, ModuleSchemeTmp::setHasAccess);
        
        binder.bind(calculationRule, ModuleSchemeTmp::getCalculationRule, ModuleSchemeTmp::setCalculationRule);

        /* -- Click Listeners for the Buttons -- */
        save.addClickListener(event -> {
            if (binder.writeBeanIfValid(moduleSchemeBeingEdited)) {
                LOGGER.info("Saved bean values: " + moduleSchemeBeingEdited);
            } else {
                BinderValidationStatus<ModuleSchemeTmp> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct().collect(Collectors.joining(", "));
                LOGGER.info("There are errors: " + errorText);
            }
        });
        reset.addClickListener(event -> {
            /* clear fields by setting null */
            binder.readBean(null);
        });

        /* -- Add Layout to Component -- */
        add(layoutWithBinder);
    }

}