package de.bp2019.pusl.ui.views.institute;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;

/**
 * View containing a form to edit a Institute
 * 
 * @author Leon Chemnitz
 */
@PageTitle(AppConfig.NAME + " | Institut bearbeiten")
@Route(value = EditInstituteView.ROUTE, layout = MainAppView.class)
public class EditInstituteView extends BaseView implements HasUrlParameter<String> {

        private static final long serialVersionUID = 1L;

        public static final String ROUTE = "edit-institute";

        /*
         * no @Autowire because service is injected by constructor. Vaadin likes it
         * better this way...
         */
        private InstituteService instituteService;

        /** Binder to bind the form Data to an Object */
        private Binder<Institute> binder;

        /**
         * null if a new Institute is being created
         */
        private ObjectId objectId;

        @Autowired
        public EditInstituteView(InstituteService instituteService) {
                super("Institut bearbeiten");

                this.instituteService = instituteService;

                FormLayout form = new FormLayout();
                form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
                form.setWidth("100%");
                form.getStyle().set("marginLeft", "1em");
                form.getStyle().set("marginTop", "-0.5em");

                binder = new Binder<>();

                /* ########### Create the fields ########### */

                TextField name = new TextField();
                name.setLabel("Name");
                name.setPlaceholder("Name des Instituts");
                name.setValueChangeMode(ValueChangeMode.EAGER);
                form.add(name, 1);

                Button save = new Button("Speichern");
                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                VerticalLayout actions = new VerticalLayout();
                actions.add(save);
                actions.setHorizontalComponentAlignment(Alignment.END, save);
                form.add(actions, 2);

                /* ########### Data Binding and validation ########### */

                binder.forField(name)
                                .withValidator(new StringLengthValidator("Bitte Name des Instituts angeben", 1, null))
                                .bind(Institute::getName, Institute::setName);

                /* ########### Click Listeners for Buttons ########### */

                save.addClickListener(event -> {
                        Institute institute = new Institute();
                        if (binder.writeBeanIfValid(institute)) {
                                if (objectId != null) {
                                        institute.setId(objectId);
                                }
                                try {
                                        instituteService.save(institute);
                                        Dialog dialog = new Dialog();
                                        dialog.add(new Text("Institut erfolgreich gespeichert"));
                                        UI.getCurrent().navigate(ManageInstitutesView.ROUTE);
                                        dialog.open();
                                } finally {
                                        // TODO: implement ErrorHandeling
                                }
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
        }

        @Override
        public void setParameter(BeforeEvent event, String instituteId) {
                if (instituteId.equals("new")) {
                        objectId = null;
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {
                        Institute fetchedInstitute = instituteService.getInstituteById(instituteId);
                        /* getInstituteById returns null if no matching Institute is found */
                        if (fetchedInstitute == null) {
                                throw new NotFoundException();
                        } else {
                                objectId = fetchedInstitute.getId();
                                binder.readBean(fetchedInstitute);
                        }
                }
        }

}