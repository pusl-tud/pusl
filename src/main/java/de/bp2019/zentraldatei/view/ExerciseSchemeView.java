package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.InstituteService;
import de.bp2019.zentraldatei.view.components.TokenEditor;
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

@PageTitle("Zentraldatei | Übungsschema")
@Route(value = "exerciseScheme", layout = MainAppView.class)

public class ExerciseSchemeView extends Div implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseSchemeView.class);

    private Binder<ExerciseScheme> binder;

    /* Binder to bind the form Data to an Object */
    private ExerciseSchemeService exerciseSchemeService;

    /*
     * set if a new MoudleScheme is being created, not set if an existing
     * ModuleScheme is being edited
     */
    private boolean isNewEntity;

    public ExerciseSchemeView(@Autowired ExerciseSchemeService exerciseSchemeService, @Autowired InstituteService instituteService) {

        LOGGER.debug("Started creation of ExerciseSchemeView");

        this.exerciseSchemeService = exerciseSchemeService;

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("5em", 1), new FormLayout.ResponsiveStep("5em", 2));
        form.setWidth("40em");
        form.getStyle().set("marginLeft", "3em");

        binder = new Binder<>();

        /*  Create the fields  */
        TextField name = new TextField("Name", "Name der Übung");
        name.setValueChangeMode(ValueChangeMode.EAGER);
        form.setWidth("40em");
        form.getStyle().set("marginLeft", "3em");
        form.add(name,1);

        MultiselectComboBox<String> institutes = new MultiselectComboBox<String>();
        institutes.setLabel("Institute");
        institutes.setItems(instituteService.getAllInstituteIDs());
        institutes.setItemLabelGenerator(item -> instituteService.getInstituteById(item).getName());
        form.add(institutes, 2);

        /*  TODO: Tokens  */
        TokenEditor tokens = new TokenEditor(exerciseSchemeService);
        form.add(tokens, 2);

        Checkbox isNumeric = new Checkbox("Mit Note");
        form.add(isNumeric);

        Button save = new Button("Speichern");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /*  Button Bar  */
        VerticalLayout actions = new VerticalLayout();
        actions.add(save);
        actions.setHorizontalComponentAlignment(FlexComponent.Alignment.END, save);
        form.add(actions);

        /*
         * Hidden TextField to bind Id, if someone knows a cleaner Solution please
         * implement it!
         */
        TextField id = new TextField("");

        /*  Binding and validation  */
        binder.bind(id, ExerciseScheme::getId, ExerciseScheme::setId);

        binder.forField(name).withValidator(new StringLengthValidator("Bitte Namen der Übung eingeben", 1, null))
                .bind(ExerciseScheme::getName, ExerciseScheme::setName);

        binder.forField(institutes)
                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(),
                        "Bitte mind. ein Institut angeben")
                .bind(ExerciseScheme::getInstitutes, ExerciseScheme::setInstitutes);

        binder.bind(tokens, ExerciseScheme::getTokens, ExerciseScheme::setTokens);

        binder.bind(isNumeric, ExerciseScheme::getIsNumeric, ExerciseScheme::setIsNumeric);

        /*  Click-Listeners  */
        save.addClickListener(event -> {
            ExerciseScheme formData = new ExerciseScheme();
            if (binder.writeBeanIfValid(formData)) {
                Dialog dialog = new Dialog();
                if (isNewEntity) {
                    exerciseSchemeService.saveModuleScheme(formData);
                    dialog.add(new Text("Übung erfolgreich erstellt"));
                } else {
                    exerciseSchemeService.updateModuleScheme(formData);
                    dialog.add(new Text("Übung erfolgreich bearbeitet"));
                }
                UI.getCurrent().navigate("exerciseSchemes");
                dialog.open();
            } else {
                BinderValidationStatus<ExerciseScheme> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError)
                        .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                        .collect(Collectors.joining(", "));
                LOGGER.debug("There are errors: " + errorText);
            }
        });

        /*  Add Layout to Component  */

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
