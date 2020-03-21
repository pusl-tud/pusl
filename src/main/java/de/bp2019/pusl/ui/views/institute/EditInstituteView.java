package de.bp2019.pusl.ui.views.institute;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.bson.types.ObjectId;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleBySuperadmin;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.LecturesView;
import de.bp2019.pusl.ui.views.MainAppView;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * View containing a form to edit a {@link Institute}
 * 
 * @author Leon Chemnitz
 */
@PageTitle(PuslProperties.NAME + " | Institut bearbeiten")
@Route(value = EditInstituteView.ROUTE, layout = MainAppView.class)
public class EditInstituteView extends BaseView implements HasUrlParameter<String>, AccessibleBySuperadmin {

        private static final long serialVersionUID = 1L;

        public static final String ROUTE = "admin/institute";

        private InstituteService instituteService;

        /** Binder to bind the form Data to an Object */
        private Binder<Institute> binder;

        /** empty if new institute is being created */
        private Optional<ObjectId> instituteId = Optional.empty();

        public EditInstituteView() {
                super("Institut bearbeiten");

                this.instituteService = Service.get(InstituteService.class);

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

                add(form);

                /* ########### Data Binding and validation ########### */

                binder.forField(name)
                                .withValidator(new StringLengthValidator("Bitte Name des Instituts angeben", 1, null))
                                .withValidator(new StringLengthValidator(
                                                "Institutsnamen dürfen nicht länger als 50 Zeichen sein", null, 50))
                                .bind(Institute::getName, Institute::setName);

                /* ########### Listeners ########### */

                save.addClickListener(event -> {

                        if(!instituteService.checkNameAvailable(name.getValue(), instituteId)){
                                ErrorDialog.open("Name bereits vergeben");
                                return;
                        }

                        Institute institute = new Institute();

                        if (binder.writeBeanIfValid(institute)) {
                                if (instituteId.isPresent()) {
                                        institute.setId(instituteId.get());
                                }
                                try {
                                        instituteService.save(institute);
                                        UI.getCurrent().navigate(ManageInstitutesView.ROUTE);
                                        SuccessDialog.open("Institut erfolgreich gespeichert");
                                } catch (UnauthorizedException e) {
                                        ErrorDialog.open("nicht authorisiert um Institut zu speichern!");
                                }
                        } else {
                                BinderValidationStatus<Institute> validate = binder.validate();
                                String errorText = validate.getFieldValidationStatuses().stream()
                                                .filter(BindingValidationStatus::isError)
                                                .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                                                .collect(Collectors.joining(", "));
                                LOGGER.info("Institute could not be saved because of validation errors. Errors were: " + errorText);
                        }
                });

        }

        @Override
        public void setParameter(BeforeEvent event, String idParameter) {
                if (idParameter.equals("new")) {
                        instituteId = Optional.empty();
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {
                        try {
                                Institute fetchedInstitute;
                                fetchedInstitute = instituteService.getById(idParameter);                                
                                instituteId = Optional.of(fetchedInstitute.getId());
                                binder.readBean(fetchedInstitute);
                        } catch (UnauthorizedException e) {
                                event.rerouteTo(LecturesView.ROUTE);
                                UI.getCurrent().navigate(LecturesView.ROUTE);      
                                ErrorDialog.open("Nicht authorisiert um Institut zu bearbeiten!");
                        } catch (DataNotFoundException e) {                   
                                event.rerouteTo(LecturesView.ROUTE);       
                                UI.getCurrent().navigate(LecturesView.ROUTE); 
                                ErrorDialog.open("Institut nicht in Datenbank gefunden!");    
                        }
                }
        }

}