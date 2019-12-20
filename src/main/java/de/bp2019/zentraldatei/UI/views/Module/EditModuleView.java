package de.bp2019.zentraldatei.UI.views.Module;

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

import de.bp2019.zentraldatei.UI.components.ExerciseSchemeArranger;
import de.bp2019.zentraldatei.UI.views.BaseView;
import de.bp2019.zentraldatei.UI.views.MainAppView;
import de.bp2019.zentraldatei.model.exercise.ExerciseInstance;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.module.Module;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.InstituteService;
import de.bp2019.zentraldatei.service.ModuleService;
import de.bp2019.zentraldatei.service.UserService;

/**
 * View containing a form to edit a Module
 * 
 * @author Leon Chemnitz
 */
@PageTitle("Zentraldatei | Veranstaltung bearbeiten")
@Route(value = EditModuleView.ROUTE, layout = MainAppView.class)
public class EditModuleView extends BaseView implements HasUrlParameter<String> {

        private static final long serialVersionUID = -7352842685521794385L;

        public static final String ROUTE = "edit-module";

        private static final Logger LOGGER = LoggerFactory.getLogger(EditModuleView.class);

        /*
         * no @Autowire because service is injected by constructor. Vaadin likes it
         * better this way...
         */
        private ModuleService moduleService;

        /** Binder to bind the form Data to an Object */
        private Binder<Module> binder;

        /**
         * set if a new MoudleScheme is being created, not set if an existing Module is
         * being edited
         */
        private boolean isNewEntity;

        @Autowired
        public EditModuleView(InstituteService instituteService, UserService userService, ModuleService moduleService,
                        ExerciseSchemeService exerciseSchemeService) {

                super("Veranstaltung bearbeiten");

                LOGGER.debug("Started creation of ModuleView");

                this.moduleService = moduleService;

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

                MultiselectComboBox<Institute> institutes = new MultiselectComboBox<Institute>();
                institutes.setLabel("Institute");
                institutes.setItems(instituteService.getAllInstitutes());
                institutes.setItemLabelGenerator(item -> item.getName());
                form.add(institutes, 1);

                MultiselectComboBox<User> hasAccess = new MultiselectComboBox<User>();
                hasAccess.setLabel("Zugriff");
                hasAccess.setItems(userService.getAllUsers());
                hasAccess.setItemLabelGenerator(item -> UserService.getFullName(item));
                form.add(hasAccess, 2);

                HorizontalLayout exerciseSchemeLayout = new HorizontalLayout();

                ExerciseSchemeArranger exerciseSchemes = new ExerciseSchemeArranger(exerciseSchemeService);

                exerciseSchemeLayout.add(exerciseSchemes);

                TextArea calculationRule = new TextArea();
                calculationRule.setValueChangeMode(ValueChangeMode.EAGER);
                calculationRule.setLabel("Berechnungsregel");
                calculationRule.setPlaceholder("Platzhalter");
                calculationRule.setHeight("15em");
                calculationRule.setWidthFull();

                exerciseSchemeLayout.add(calculationRule);
                form.add(exerciseSchemeLayout);

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

                binder.bind(id, Module::getId, Module::setId);

                binder.forField(name).withValidator(
                                new StringLengthValidator("Bitte Name der Veranstaltung angeben", 1, null))
                                .bind(Module::getName, Module::setName);

                binder.forField(institutes)
                                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(),
                                                "Bitte mind. ein Institut angeben")
                                .bind(Module::getInstitutes, Module::setInstitutes);

                binder.bind(hasAccess, Module::getHasAccess, Module::setHasAccess);

                // binder.bind(exerciseSchemes,
                //                 module -> module.getExercises().stream().map(ExerciseInstance::getScheme)
                //                                 .collect(Collectors.toList()),
                //                 (module, schemes) -> 
                //                 {LOGGER.info(module.toString()); LOGGER.info(schemes.toString());moduleService.newExercisesfromSchemes(module, schemes);});

                binder.bind(calculationRule, Module::getCalculationRule, Module::setCalculationRule);

                /* ########### Click Listeners for Buttons ########### */

                save.addClickListener(event -> {
                        Module formData = new Module();
                        if (binder.writeBeanIfValid(formData)) {
                                Dialog dialog = new Dialog();
                                if (isNewEntity) {
                                        moduleService.saveModule(formData);
                                        dialog.add(new Text("Veranstaltungsschema erfolgreich erstellt oder so..."));
                                } else {
                                        moduleService.updateModule(formData);
                                        dialog.add(new Text("Veranstaltungsschema erfolgreich verändert oder so..."));
                                }
                                UI.getCurrent().navigate(ManageModulesView.ROUTE);
                                dialog.open();
                        } else {
                                BinderValidationStatus<Module> validate = binder.validate();
                                String errorText = validate.getFieldValidationStatuses().stream()
                                                .filter(BindingValidationStatus::isError)
                                                .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                                                .collect(Collectors.joining(", "));
                                LOGGER.debug("There are errors: " + errorText);
                        }
                });

                /* ########### Add Layout to Component ########### */

                add(form);
                LOGGER.debug("Finished creation of ManageModulesView");
        }

        @Override
        public void setParameter(BeforeEvent event, String moduleId) {
                if (moduleId.equals("new")) {
                        isNewEntity = true;
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {
                        Module fetchedModule = moduleService.getModuleById(moduleId);
                        /* getModuleById returns null if no matching Module is found */
                        if (fetchedModule == null) {
                                throw new NotFoundException();
                        } else {
                                isNewEntity = false;
                                binder.readBean(fetchedModule);
                        }
                }
        }

}