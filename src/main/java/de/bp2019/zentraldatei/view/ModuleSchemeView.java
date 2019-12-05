package de.bp2019.zentraldatei.view;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.service.InstituteService;
import de.bp2019.zentraldatei.service.ModuleSchemeService;
import de.bp2019.zentraldatei.service.UserService;

/**
 * View containing a form to edit a ModuleScheme
 * 
 * @author Leon Chemnitz
 */
@Route("moduleScheme")
public class ModuleSchemeView extends Div implements HasUrlParameter<String> {

        private static final long serialVersionUID = 1L;
        private static final Logger LOGGER = LoggerFactory.getLogger(ModuleSchemeView.class);

        ModuleSchemeService moduleSchemeService;

        /** The object that will be edited */
        private ModuleScheme moduleSchemeBeingEdited;
        /** Binder to bind the form Data to an Object */
        Binder<ModuleScheme> binder;

        public ModuleSchemeView(@Autowired InstituteService instituteService, @Autowired UserService userService,
                        @Autowired ModuleSchemeService moduleSchemeService) {

                LOGGER.debug("Started creation of ModuleSchemeView");

                this.moduleSchemeService = moduleSchemeService;

                FormLayout layoutWithBinder = new FormLayout();
                layoutWithBinder.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("10em", 2));
                layoutWithBinder.setWidth("35em");
                layoutWithBinder.getStyle().set("marginLeft", "1em");

                binder = new Binder<>();

                /* -- Create the fields -- */
                TextField name = new TextField();
                name.setLabel("Name");
                name.setPlaceholder("Name Der Veranstaltung");
                name.setValueChangeMode(ValueChangeMode.EAGER);
                layoutWithBinder.add(name);

                MultiselectComboBox<Institute> institutes = new MultiselectComboBox<Institute>();
                institutes.setLabel("Institute");
                institutes.setItems(instituteService.getAllInstitutes());
                institutes.setItemLabelGenerator(Institute::getName);
                layoutWithBinder.add(institutes, 2);

                MultiselectComboBox<User> hasAccess = new MultiselectComboBox<User>();
                hasAccess.setLabel("Hat Zugriff");
                hasAccess.setItems(userService.getAllUsers());
                hasAccess.setItemLabelGenerator(user -> userService.getFullName(user));
                layoutWithBinder.add(hasAccess, 2);

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
                binder.forField(name).withValidator(
                                new StringLengthValidator("Bitte Name der Veranstaltung angeben", 1, null))
                                .bind(ModuleScheme::getName, ModuleScheme::setName);

                binder.forField(institutes)
                                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(),
                                                "Bitte mind. ein Institut angeben")
                                .bind(ModuleScheme::getInstitutes, ModuleScheme::setInstitutes);

                binder.bind(hasAccess, ModuleScheme::getHasAccess, ModuleScheme::setHasAccess);

                binder.bind(calculationRule, ModuleScheme::getCalculationRule, ModuleScheme::setCalculationRule);

                /* -- Click Listeners for the Buttons -- */
                save.addClickListener(event -> {
                        if (binder.writeBeanIfValid(moduleSchemeBeingEdited)) {
                                LOGGER.info("Saved bean values: " + moduleSchemeBeingEdited);
                        } else {
                                BinderValidationStatus<ModuleScheme> validate = binder.validate();
                                String errorText = validate.getFieldValidationStatuses().stream()
                                                .filter(BindingValidationStatus::isError)
                                                .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                                                .collect(Collectors.joining(", "));
                                LOGGER.info("There are errors: " + errorText);
                        }
                });
                reset.addClickListener(event -> {
                        /* clear fields by setting null */
                        binder.readBean(null);
                });

                /* -- Add Layout to Component -- */
                add(layoutWithBinder);
                LOGGER.debug("Finished creation of ManageModuleSchemesView");
        }

        @Override
        public void setParameter(BeforeEvent event, String moduleSchemeId) {
                if (moduleSchemeId.equals("new")) {
                        moduleSchemeBeingEdited = new ModuleScheme();
                } else {
                        ModuleScheme fetchedModuleScheme = moduleSchemeService.getModuleSchemeById(moduleSchemeId);

                        /* getModuleSchemeById returns null if no matching ModuleScheme is found */
                        if (fetchedModuleScheme == null) {
                                throw new NotFoundException();
                        } else {
                                moduleSchemeBeingEdited = fetchedModuleScheme;
                                binder.readBean(fetchedModuleScheme);
                        }
                }

        }

}