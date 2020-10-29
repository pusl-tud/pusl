package de.bp2019.pusl.ui.views.exercisescheme;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.textfield.NumberField;
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
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.ui.components.TokenEditor;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 *
 * @author Luca Dinies, Leon Chemnitz
 *
 **/

@PageTitle(PuslProperties.NAME + " | Übungsschema bearbeiten")
@Route(value = EditExerciseSchemeView.ROUTE, layout = MainAppView.class)
public class EditExerciseSchemeView extends BaseView implements HasUrlParameter<String>, AccessibleByAdmin {

    private static final long serialVersionUID = -1771968129664884637L;

    public static final String ROUTE = "admin/exerciseScheme";

    private Binder<ExerciseScheme> binder;

    private ExerciseSchemeService exerciseSchemeService;
    private InstituteService instituteService;

    /** empty if new institute is being created */
    private Optional<ObjectId> exerciseSchemeId = Optional.empty();

    private ComboBox<Token> defaultValueToken;
    private NumberField defaultValueNumeric;

    public EditExerciseSchemeView() {
        super("Übungsschema bearbeiten");

        this.exerciseSchemeService = Service.get(ExerciseSchemeService.class);
        this.instituteService = Service.get(InstituteService.class);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
        form.setWidth("calc(100% - 1em)");
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

        defaultValueNumeric = new NumberField();
        defaultValueNumeric.setLabel("Standard Note");
        defaultValueNumeric.setId("default-value-numeric");

        defaultValueToken = new ComboBox<>();
        defaultValueToken.setLabel("Standard Token");
        defaultValueToken.setId("default-value-token");
        defaultValueToken.setItemLabelGenerator(Token::getName);
        defaultValueToken.setClearButtonVisible(false);
        defaultValueToken.setVisible(false);

        Checkbox tokenBased = new Checkbox("Token basiert");
        tokenBased.setId("token-based");

        TokenEditor tokens = new TokenEditor(exerciseSchemeService);
        tokens.setId("token");
        tokens.setVisible(false);

        form.add(name, 1);
        form.add(institutes, 1);
        form.add(defaultValueNumeric, 1);
        form.add(defaultValueToken, 1);
        form.add(tokenBased, 1);
        form.add(tokens, 2);

        add(form);

        Button save = new Button("Speichern");
        save.setId("save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(save);
        setHorizontalComponentAlignment(Alignment.END, save);

        binder.forField(name).withValidator(new StringLengthValidator("Bitte Namen der Übung eingeben", 1, null))
                .bind(ExerciseScheme::getName, ExerciseScheme::setName);

        binder.forField(institutes)
                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(), "Bitte mind. ein Institut angeben")
                .bind(instituteService::getInstitutesFromObject, instituteService::setInstitutesToObject);

        binder.forField(defaultValueNumeric).withValidator(value -> {
            if (!tokenBased.getValue()) {
                return value != null;
            }
            return true;
        }, "Bitte Standard Note auswählen").bind(ExerciseScheme::getDefaultValueNumeric,
                ExerciseScheme::setDefaultValueNumeric);

        binder.forField(defaultValueToken).withValidator(token -> {
            if (tokenBased.getValue()) {
                return token != null;
            }
            return true;
        }, "Bitte Standard Token auswählen").bind(ExerciseScheme::getDefaultValueToken,
                ExerciseScheme::setDefaultValueToken);

        binder.bind(tokenBased, es -> !es.isNumeric(), (es, value) -> es.setIsNumeric(!value));

        binder.bind(tokens, ExerciseScheme::getTokens, ExerciseScheme::setTokens);

        /* ########### Listeners ########## */
        tokenBased.addValueChangeListener(evt -> {
            if (tokenBased.getValue()) {
                defaultValueNumeric.setVisible(false);
                defaultValueToken.setVisible(true);
                tokens.setVisible(true);
            } else {
                defaultValueNumeric.setVisible(true);
                defaultValueToken.setVisible(false);
                tokens.setVisible(false);
            }
        });

        tokens.addValueChangeListener(event -> {
            LOGGER.debug("TokenEditor changed");

            Set<Token> tokenSet = event.getValue();
            Token defaultToken = defaultValueToken.getValue();

            defaultValueToken.setItems(tokenSet);
            if (tokenSet.contains(defaultToken)) {
                defaultValueToken.setValue(defaultToken);
            }
        });

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
                    LOGGER.error("unauthorized to edit ExerciseScheme");
                    ErrorDialog.open("nicht authorisiert um Übungsschema zu speichern!");
                }
            } else {
                BinderValidationStatus<ExerciseScheme> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct().collect(Collectors.joining(", "));
                LOGGER.info(
                        "ExerciseScheme could not be saved because of validation errors. Errors were: " + errorText);
            }
        });
    }

    @Override
    public void setParameter(BeforeEvent event, String idParameter) {
        if (idParameter.equals("new")) {
            exerciseSchemeId = Optional.empty();
            
            
            binder.readBean(new ExerciseScheme());
            //binder.readBean(null);
        } else {
            try {
                ExerciseScheme fetchedExerciseScheme;
                fetchedExerciseScheme = exerciseSchemeService.getById(idParameter);
                exerciseSchemeId = Optional.of(fetchedExerciseScheme.getId());
                defaultValueToken.setItems(fetchedExerciseScheme.getTokens());

                binder.readBean(fetchedExerciseScheme);
            } catch (UnauthorizedException e) {
                event.rerouteTo(PuslProperties.ROOT_ROUTE);
                UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
                ErrorDialog.open("Nicht authorisiert um Übungsschema zu bearbeiten!");
            } catch (DataNotFoundException e) {
                event.rerouteTo(PuslProperties.ROOT_ROUTE);
                UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
                ErrorDialog.open("Übungsschema nicht in Datenbank gefunden!");
            }
        }
    }

}
