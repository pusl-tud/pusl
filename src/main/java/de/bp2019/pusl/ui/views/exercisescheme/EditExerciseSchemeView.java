package de.bp2019.pusl.ui.views.exercisescheme;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.ui.components.TokenEditor;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.LecturesView;
import de.bp2019.pusl.ui.views.MainAppView;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 *
 * @author Luca Dinies
 *
 **/

@PageTitle(PuslProperties.NAME + " | Übungsschema bearbeiten")
@Route(value = EditExerciseSchemeView.ROUTE, layout = MainAppView.class)
public class EditExerciseSchemeView extends BaseView implements HasUrlParameter<String>, AccessibleByAdmin {

    private static final long serialVersionUID = -1771968129664884637L;

    public static final String ROUTE = "admin/exerciseScheme";

    private Binder<ExerciseScheme> binder;

    /* Binder to bind the form Data to an Object */
    private ExerciseSchemeService exerciseSchemeService;

    /** empty if new institute is being created */
    private Optional<ObjectId> exerciseSchemeId = Optional.empty();

    @Autowired
    public EditExerciseSchemeView(ExerciseSchemeService exerciseSchemeService, InstituteService instituteService) {
        super("Übungsschema bearbeiten");

        this.exerciseSchemeService = exerciseSchemeService;

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
        form.setWidth("100%");
        form.getStyle().set("marginLeft", "1em");
        form.getStyle().set("marginTop", "-0.5em");

        binder = new Binder<>();

        /* Create the fields */
        TextField name = new TextField("Name", "Name der Übung");
        name.setId("name");
        name.setValueChangeMode(ValueChangeMode.EAGER);

        MultiselectComboBox<Institute> institutes = new MultiselectComboBox<Institute>();
        institutes.setLabel("Institute");
        institutes.setDataProvider(instituteService);
        institutes.setItemLabelGenerator(Institute::getName);
        institutes.setId("institutes");

        TokenEditor tokens = new TokenEditor(exerciseSchemeService);
        tokens.setId("token");

        Checkbox isNumeric = new Checkbox("Mit Note");
        isNumeric.addValueChangeListener(evt -> {
            if (isNumeric.getValue()) {
                tokens.setVisible(false);
            } else {
                tokens.setVisible(true);
            }
        });
        isNumeric.setId("numeric");

        Checkbox flexHandin = new Checkbox("Einzel-Ausgabe");
        flexHandin.setId("flex-Handin");

        TextField defaultValue = new TextField();
        defaultValue.setLabel("Standart Wert");
        defaultValue.setId("default-Value");

        form.add(name, 1);
        form.add(institutes, 1);
        form.add(isNumeric);
        form.add(flexHandin);
        form.add(defaultValue);
        form.add(tokens, 2);

        Button save = new Button("Speichern");
        save.setId("save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /* Button Bar */
        VerticalLayout actions = new VerticalLayout();
        actions.add(save);
        actions.setHorizontalComponentAlignment(FlexComponent.Alignment.END, save);
        form.add(actions);

        binder.forField(name).withValidator(new StringLengthValidator("Bitte Namen der Übung eingeben", 1, null))
                .bind(ExerciseScheme::getName, ExerciseScheme::setName);

        binder.forField(institutes)
                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(), "Bitte mind. ein Institut angeben")
                .bind(ExerciseScheme::getInstitutes, ExerciseScheme::setInstitutes);

        binder.bind(tokens, ExerciseScheme::getTokens, ExerciseScheme::setTokens);

        binder.bind(isNumeric, ExerciseScheme::getIsNumeric, ExerciseScheme::setIsNumeric);

        binder.bind(flexHandin, ExerciseScheme::isFlexHandin, ExerciseScheme::setFlexHandin);

        binder.forField(defaultValue)
                .withValidator(new StringLengthValidator("Bitte Standart-Bewertung eingeben!", 1, null))
                .bind(ExerciseScheme::getDefaultValue, ExerciseScheme::setDefaultValue);

        /* Click-Listeners */
        save.addClickListener(event -> {

            if (!exerciseSchemeService.checkNameAvailable(name.getValue(), exerciseSchemeId)) {
                ErrorDialog.open("Name bereits vergeben");
                return;
            }

            ExerciseScheme exerciseScheme = new ExerciseScheme();

            if (binder.writeBeanIfValid(exerciseScheme)) {
                if (exerciseSchemeId.isPresent()) {
                    exerciseScheme.setId(exerciseSchemeId.get());
                }
                try {
                    exerciseSchemeService.save(exerciseScheme);
                    UI.getCurrent().navigate(ManageExerciseSchemesView.ROUTE);
                    SuccessDialog.open("Übungsschema erfolgreich gespeichert");
                } catch (UnauthorizedException e) {
                    ErrorDialog.open("nicht authorisiert um Übungsschema zu speichern!");
                }
            } else {
                BinderValidationStatus<ExerciseScheme> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct().collect(Collectors.joining(", "));
                LOGGER.info("ExerciseScheme could not be saved because of validation errors. Errors were: " + errorText);
            }
        });

        /* Add Layout to Component */

        add(form);
    }

    @Override
    public void setParameter(BeforeEvent event, String idParameter) {
        if (idParameter.equals("new")) {
            exerciseSchemeId = Optional.empty();
            /* clear fields by setting null */
            binder.readBean(null);
        } else {
            try {
                ExerciseScheme fetchedInstitute;
                fetchedInstitute = exerciseSchemeService.getById(idParameter);
                exerciseSchemeId = Optional.of(fetchedInstitute.getId());
                binder.readBean(fetchedInstitute);
            } catch (UnauthorizedException e) {
                event.rerouteTo(LecturesView.ROUTE);
                UI.getCurrent().navigate(LecturesView.ROUTE);
                ErrorDialog.open("Nicht authorisiert um Übungsschema zu bearbeiten!");
            } catch (DataNotFoundException e) {
                event.rerouteTo(LecturesView.ROUTE);
                UI.getCurrent().navigate(LecturesView.ROUTE);
                ErrorDialog.open("Übungsschema nicht in Datenbank gefunden!");
            }
        }
    }

}
