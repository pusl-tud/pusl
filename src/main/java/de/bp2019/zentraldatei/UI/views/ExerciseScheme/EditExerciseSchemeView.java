package de.bp2019.zentraldatei.UI.views.ExerciseScheme;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import de.bp2019.zentraldatei.UI.components.TokenEditor;
import de.bp2019.zentraldatei.UI.views.BaseView;
import de.bp2019.zentraldatei.UI.views.MainAppView;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.exercise.ExerciseScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.InstituteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Luca Dinies
 *
 **/

@PageTitle("Zentraldatei | Übungsschema bearbeiten")
@Route(value = EditExerciseSchemeView.ROUTE, layout = MainAppView.class)
public class EditExerciseSchemeView extends BaseView implements HasUrlParameter<String> {

    private static final long serialVersionUID = -1771968129664884637L;

    public static final String ROUTE = "edit-exerciseScheme";

    private static final Logger LOGGER = LoggerFactory.getLogger(EditExerciseSchemeView.class);

    private Binder<ExerciseScheme> binder;

    /* Binder to bind the form Data to an Object */
    private ExerciseSchemeService exerciseSchemeService;

    /*
     * set if a new ExerciseScheme is being created, not set if an existing
     * ExerciseScheme is being edited
     */
    private boolean isNewEntity;

    @Autowired
    public EditExerciseSchemeView(ExerciseSchemeService exerciseSchemeService, InstituteService instituteService) {
        super("Übungsschema bearbeiten");
        LOGGER.debug("Started creation of ExerciseSchemeView");

        this.exerciseSchemeService = exerciseSchemeService;

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
        form.setWidth("100%");
        form.getStyle().set("marginLeft", "1em");
        form.getStyle().set("marginTop", "-0.5em");

        binder = new Binder<>();

        /* Create the fields */
        TextField name = new TextField("Name", "Name der Übung");
        name.setValueChangeMode(ValueChangeMode.EAGER);

        MultiselectComboBox<Institute> institutes = new MultiselectComboBox<Institute>();
        institutes.setLabel("Institute");
        institutes.setItems(instituteService.getAllInstitutes());
        institutes.setItemLabelGenerator(Institute::getName);

        TokenEditor tokens = new TokenEditor(exerciseSchemeService);

        Checkbox isNumeric = new Checkbox("Mit Note");
        isNumeric.addValueChangeListener(evt -> {
            if(isNumeric.getValue()){
                tokens.setVisible(false);
            } else {
                tokens.setVisible(true);
            }
        });

        Checkbox flexHandin = new Checkbox("Einzel-Ausgabe");

        TextField defaultValue = new TextField();
        defaultValue.setLabel("Standart Wert");


        form.add(name, 1);
        form.add(institutes, 1);
        form.add(isNumeric);
        form.add(flexHandin);
        form.add(defaultValue);
        form.add(tokens, 2);

        Button save = new Button("Speichern");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /* Button Bar */
        VerticalLayout actions = new VerticalLayout();
        actions.add(save);
        actions.setHorizontalComponentAlignment(FlexComponent.Alignment.END, save);
        form.add(actions);

        /*
         * Hidden TextField to bind Id, if someone knows a cleaner Solution please
         * implement it!
         */
        TextField id = new TextField("");

        /* Binding and validation */
        binder.bind(id, ExerciseScheme::getId, ExerciseScheme::setId);

        binder.forField(name).withValidator(new StringLengthValidator("Bitte Namen der Übung eingeben", 1, null))
                .bind(ExerciseScheme::getName, ExerciseScheme::setName);

        binder.forField(institutes)
                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(), "Bitte mind. ein Institut angeben")
                .bind(ExerciseScheme::getInstitutes, ExerciseScheme::setInstitutes);

        binder.bind(tokens, ExerciseScheme::getTokens, ExerciseScheme::setTokens);

        binder.bind(isNumeric, ExerciseScheme::getIsNumeric, ExerciseScheme::setIsNumeric);

        binder.bind(flexHandin, ExerciseScheme::isFlexHandin, ExerciseScheme::setFlexHandin);

        binder.forField(defaultValue).withValidator(new StringLengthValidator("Bitte Standart-Bewertung eingeben!", 1, null))
                .bind(ExerciseScheme::getDefaultValue, ExerciseScheme::setDefaultValue);

        /* Click-Listeners */
        save.addClickListener(event -> {
            ExerciseScheme formData = new ExerciseScheme();
            if (binder.writeBeanIfValid(formData)) {
                Dialog dialog = new Dialog();
                if (isNewEntity) {
                    exerciseSchemeService.saveExerciseScheme(formData);
                    dialog.add(new Text("Übung erfolgreich erstellt"));
                } else {
                    exerciseSchemeService.updateExerciseScheme(formData);
                    dialog.add(new Text("Übung erfolgreich bearbeitet"));
                }
                UI.getCurrent().navigate(ManageExerciseSchemesView.ROUTE);
                dialog.open();
            } else {
                BinderValidationStatus<ExerciseScheme> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct().collect(Collectors.joining(", "));
                LOGGER.debug("There are errors: " + errorText);
            }
        });

        /* Add Layout to Component */

        add(form);
        LOGGER.debug("Finished creation of ManageExerciseSchemesView");

    }

    @Override
    public void setParameter(BeforeEvent event, String exerciseSchemeId) {
        if (exerciseSchemeId.equals("new")) {
            isNewEntity = true;
            /* clear fields by setting null */
            binder.readBean(null);
        } else {
            ExerciseScheme fetchedExerciseScheme = exerciseSchemeService.getExerciseSchemeById(exerciseSchemeId);
            /* getExerciseSchemeById returns null if no matching ExerciseScheme is found */
            if (fetchedExerciseScheme == null) {
                throw new NotFoundException();
            } else {
                isNewEntity = false;
                binder.readBean(fetchedExerciseScheme);
            }
        }
    }

}
