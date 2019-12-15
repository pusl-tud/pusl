package de.bp2019.zentraldatei.view;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.InstituteService;
import de.bp2019.zentraldatei.service.ModuleSchemeService;
import de.bp2019.zentraldatei.service.UserService;
import de.bp2019.zentraldatei.view.components.ExerciseSchemeArranger;

/**
 * View containing a form to edit a ModuleScheme
 * 
 * @author Leon Chemnitz
 */
@PageTitle("Zentraldatei | Veranstaltungsschema")
@Route(value = "moduleScheme", layout = MainAppView.class)
public class ModuleSchemeView extends BaseView implements HasUrlParameter<String> {

        private static final long serialVersionUID = -7352842685521794385L;

        private static final Logger LOGGER = LoggerFactory.getLogger(ModuleSchemeView.class);

        /*
         * no @Autowire because service is injected by constructor. Vaadin likes it
         * better this way...
         */
        private ModuleSchemeService moduleSchemeService;

        /** Binder to bind the form Data to an Object */
        private Binder<ModuleScheme> binder;

        /**
         * set if a new MoudleScheme is being created, not set if an existing
         * ModuleScheme is being edited
         */
        private boolean isNewEntity;

        public ModuleSchemeView(@Autowired InstituteService instituteService, @Autowired UserService userService,
                        @Autowired ModuleSchemeService moduleSchemeService,
                        @Autowired ExerciseSchemeService exerciseSchemeService) {

                super("Veranstaltungsschema bearbeiten");

                LOGGER.debug("Started creation of ModuleSchemeView");

                this.moduleSchemeService = moduleSchemeService;

                FormLayout form = new FormLayout();
                form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
                form.setWidth("100%");
                form.getStyle().set("marginLeft", "1em");
                form.getStyle().set("marginTop", "-0.5em");

                binder = new Binder<>();

                /* ########### Create the fields ########### */

                TextField name = new TextField();
                name.setLabel("Name");
                name.setPlaceholder("Name Der Veranstaltung");
                name.setValueChangeMode(ValueChangeMode.EAGER);
                form.add(name, 1);

                MultiselectComboBox<String> institutes = new MultiselectComboBox<String>();
                institutes.setLabel("Institute");
                institutes.setItems(instituteService.getAllInstituteIDs());
                institutes.setItemLabelGenerator(item -> instituteService.getInstituteById(item).getName());
                form.add(institutes, 1);

                MultiselectComboBox<String> hasAccess = new MultiselectComboBox<String>();
                hasAccess.setLabel("Zugriff");
                hasAccess.setItems(userService.getAllUserIDs());
                hasAccess.setItemLabelGenerator(item -> userService.getFullNameById(item));
                form.add(hasAccess, 2);

                HorizontalLayout exerciseSchemeLayout = new HorizontalLayout();

                ExerciseSchemeArranger exerciseSchemes = new ExerciseSchemeArranger(exerciseSchemeService);
                // layoutWithBinder.add(exerciseSchemes, 1);
                exerciseSchemeLayout.add(exerciseSchemes);

                TextArea calculationRule = new TextArea();
                calculationRule.setValueChangeMode(ValueChangeMode.EAGER);
                calculationRule.setLabel("Berechnungsregel");
                calculationRule.setPlaceholder("Platzhalter");
                calculationRule.setHeight("15em");
                calculationRule.setWidthFull();

                exerciseSchemeLayout.add(calculationRule);
                form.add(exerciseSchemeLayout);
                // layoutWithBinder.add(calculationRule, 1);
                form.add(exerciseSchemeLayout, 2);

                Button save = new Button("Speichern");
                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                VerticalLayout actions = new VerticalLayout();
                actions.add(save);
                actions.setHorizontalComponentAlignment(Alignment.END, save);
                form.add(actions, 2);

                /*
                 * Hidden TextField to bind Id, if someone knows a cleaner Solution please
                 * implement it!
                 */
                TextField id = new TextField("");

                /* ########### Data Binding and validation ########### */

                binder.bind(id, ModuleScheme::getId, ModuleScheme::setId);

                binder.forField(name).withValidator(
                                new StringLengthValidator("Bitte Name der Veranstaltung angeben", 1, null))
                                .bind(ModuleScheme::getName, ModuleScheme::setName);

                binder.forField(institutes)
                                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(),
                                                "Bitte mind. ein Institut angeben")
                                .bind(ModuleScheme::getInstitutes, ModuleScheme::setInstitutes);

                binder.bind(hasAccess, ModuleScheme::getHasAccess, ModuleScheme::setHasAccess);

                binder.bind(exerciseSchemes, ModuleScheme::getExerciseSchemes, ModuleScheme::setExerciseSchemes);

                binder.bind(calculationRule, ModuleScheme::getCalculationRule, ModuleScheme::setCalculationRule);

                /* ########### Click Listeners for Buttons ########### */

                save.addClickListener(event -> {
                        ModuleScheme formData = new ModuleScheme();
                        if (binder.writeBeanIfValid(formData)) {
                                Dialog dialog = new Dialog();
                                if (isNewEntity) {
                                        moduleSchemeService.saveModuleScheme(formData);
                                        dialog.add(new Text("Veranstaltungsschema erfolgreich erstellt oder so..."));
                                } else {
                                        moduleSchemeService.updateModuleScheme(formData);
                                        dialog.add(new Text("Veranstaltungsschema erfolgreich verändert oder so..."));
                                }
                                UI.getCurrent().navigate("moduleSchemes");
                                dialog.open();
                        } else {
                                BinderValidationStatus<ModuleScheme> validate = binder.validate();
                                String errorText = validate.getFieldValidationStatuses().stream()
                                                .filter(BindingValidationStatus::isError)
                                                .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                                                .collect(Collectors.joining(", "));
                                LOGGER.debug("There are errors: " + errorText);
                        }
                });

                /* ########### Add Layout to Component ########### */

                add(form);
                LOGGER.debug("Finished creation of ManageModuleSchemesView");
        }

        @Override
        public void setParameter(BeforeEvent event, String moduleSchemeId) {
                if (moduleSchemeId.equals("new")) {
                        isNewEntity = true;
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {
                        ModuleScheme fetchedModuleScheme = moduleSchemeService.getModuleSchemeById(moduleSchemeId);
                        /* getModuleSchemeById returns null if no matching ModuleScheme is found */
                        if (fetchedModuleScheme == null) {
                                throw new NotFoundException();
                        } else {
                                isNewEntity = false;
                                binder.readBean(fetchedModuleScheme);
                        }
                }
        }

}