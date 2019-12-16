package de.bp2019.zentraldatei.UI.views.Institute;

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
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.InstituteService;
import de.bp2019.zentraldatei.service.ModuleSchemeService;
import de.bp2019.zentraldatei.service.UserService;

/**
 * View containing a form to edit a ModuleScheme
 * 
 * @author Leon Chemnitz
 */
@PageTitle("Zentraldatei | Institut")
@Route(value = "institute", layout = MainAppView.class)
public class InstituteView extends BaseView implements HasUrlParameter<String> {

        private static final long serialVersionUID = 1L;

        private static final Logger LOGGER = LoggerFactory.getLogger(InstituteView.class);

        /*
         * no @Autowire because service is injected by constructor. Vaadin likes it
         * better this way...
         */
        private InstituteService instituteService;

        /** Binder to bind the form Data to an Object */
        private Binder<Institute> binder;

        /**
         * set if a new Institute is being created, not set if an existing Institute is
         * being edited
         */
        private boolean isNewEntity;

        @Autowired
        public InstituteView(InstituteService instituteService) {
                super("Institut bearbeiten");

                this.instituteService = instituteService;

                LOGGER.debug("Started creation of InstituteView");

                FormLayout form = new FormLayout();
                form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
                form.setWidth("100%");
                form.getStyle().set("marginLeft", "1em");
                form.getStyle().set("marginTop", "-0.5em");

                binder = new Binder<>();

                /* ########### Create the fields ########### */

                TextField name = new TextField();
                name.setLabel("Name");
                name.setPlaceholder("Name Des Instituts");
                name.setValueChangeMode(ValueChangeMode.EAGER);
                form.add(name, 1);

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

                binder.bind(id, Institute::getId, Institute::setId);

                binder.forField(name)
                                .withValidator(new StringLengthValidator("Bitte Name des Instituts angeben", 1, null))
                                .bind(Institute::getName, Institute::setName);

                /* ########### Click Listeners for Buttons ########### */

                save.addClickListener(event -> {
                        Institute formData = new Institute();
                        if (binder.writeBeanIfValid(formData)) {
                                Dialog dialog = new Dialog();
                                if (isNewEntity) {
                                        instituteService.saveInstitute(formData);
                                        dialog.add(new Text("Institut erfolgreich erstellt oder so..."));
                                } else {
                                        instituteService.updateInstitute(formData);
                                        dialog.add(new Text("Institut erfolgreich verändert oder so..."));
                                }
                                UI.getCurrent().navigate("institutes");
                                dialog.open();
                        } else {
                                BinderValidationStatus<Institute> validate = binder.validate();
                                String errorText = validate.getFieldValidationStatuses().stream()
                                                .filter(BindingValidationStatus::isError)
                                                .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                                                .collect(Collectors.joining(", "));
                                LOGGER.debug("There are errors: " + errorText);
                        }
                });

                /* ########### Add Layout to Component ########### */

                add(form);
                LOGGER.debug("Finished creation of InstituteView");
        }

        @Override
        public void setParameter(BeforeEvent event, String instituteId) {
                if (instituteId.equals("new")) {
                        isNewEntity = true;
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {
                        Institute fetchedInstitute = instituteService.getInstituteById(instituteId);
                        /* getModuleSchemeById returns null if no matching ModuleScheme is found */
                        if (fetchedInstitute == null) {
                                throw new NotFoundException();
                        } else {
                                isNewEntity = false;
                                binder.readBean(fetchedInstitute);
                        }
                }
        }

}