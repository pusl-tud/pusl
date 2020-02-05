package de.bp2019.pusl.ui.views.lecture;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
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
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.components.ExerciseComposer;
import de.bp2019.pusl.ui.components.PerformanceSchemeComposer;
import de.bp2019.pusl.ui.components.VerticalTabs;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;

/**
 * View containing a form to edit a Lecture
 * 
 * @author Leon Chemnitz
 */
@PageTitle(AppConfig.NAME + " | Veranstaltung bearbeiten")
@Route(value = EditLectureView.ROUTE, layout = MainAppView.class)
public class EditLectureView extends BaseView implements HasUrlParameter<String> {

        private static final long serialVersionUID = -7352842685521794385L;

        public static final String ROUTE = "admin/lecture";

        /*
         * no @Autowire because service is injected by constructor. Vaadin likes it
         * better this way...
         */
        private LectureService lectureService;

        /** Binder to bind the form Data to an Object */
        private Binder<Lecture> binder;

        /**
         * null if a new {@link Lecture} is being created
         */
        private ObjectId objectId;

        @Autowired
        public EditLectureView(InstituteService instituteService, UserService userService,
                        LectureService lectureService, ExerciseSchemeService exerciseSchemeService) {

                super("Veranstaltung bearbeiten");

                this.lectureService = lectureService;

                FormLayout formLayout = new FormLayout();
                formLayout.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
                formLayout.setWidth("calc(100% - 1em)");
                formLayout.getStyle().set("marginLeft", "1em");
                formLayout.getStyle().set("marginTop", "-0.5em");

                binder = new Binder<>();

                /* ########### Create the fields ########### */

                TextField name = new TextField();
                name.setLabel("Name");
                name.setPlaceholder("Name der Veranstaltung");
                name.setValueChangeMode(ValueChangeMode.EAGER);
                formLayout.add(name, 1);

                MultiselectComboBox<Institute> institutes = new MultiselectComboBox<Institute>();
                institutes.setLabel("Institute");
                institutes.setItems(instituteService.getAllInstitutes());
                institutes.setItemLabelGenerator(item -> item.getName());
                formLayout.add(institutes, 1);

                VerticalTabs verticalTabs = new VerticalTabs();
                verticalTabs.setHeight("25em");
                verticalTabs.setWidth("100%");

                ExerciseComposer exercises = new ExerciseComposer(exerciseSchemeService);
                verticalTabs.addTab("Prüfungen", exercises);

                PerformanceSchemeComposer performanceSchemes = new PerformanceSchemeComposer();
                verticalTabs.addTab("Leistungen", performanceSchemes);

                MultiselectComboBox<User> hasAccess = new MultiselectComboBox<User>();
                hasAccess.setWidth("100%");
                hasAccess.setHeight("10em");
                hasAccess.setLabel("Zugriff");
                hasAccess.setItems(userService.getAll());
                hasAccess.setItemLabelGenerator(item -> UserService.getFullName(item));
                verticalTabs.addTab("Zugriff", hasAccess);

                formLayout.add(verticalTabs, 2);

                add(formLayout);

                Button save = new Button("Speichern");
                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                add(save);
                setHorizontalComponentAlignment(Alignment.END, save);

                /* ########### Data Binding and validation ########### */
                binder.forField(name).withValidator(
                                new StringLengthValidator("Bitte Name der Veranstaltung angeben", 1, null))
                                .bind(Lecture::getName, Lecture::setName);

                binder.forField(institutes)
                                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(),
                                                "Bitte mind. ein Institut angeben")
                                .bind(Lecture::getInstitutes, Lecture::setInstitutes);

                binder.bind(hasAccess, Lecture::getHasAccess, Lecture::setHasAccess);

                binder.bind(exercises, Lecture::getExercises, Lecture::setExercises);

                binder.bind(performanceSchemes, Lecture::getPerformanceSchemes, Lecture::setPerformanceSchemes);

                /* ########### Click Listeners for Buttons ########### */

                save.addClickListener(event -> {
                        Lecture lecture = new Lecture();
                        if (binder.writeBeanIfValid(lecture)) {
                                if (objectId != null) {
                                        lecture.setId(objectId);
                                }
                                try {
                                        lectureService.save(lecture);
                                        Dialog dialog = new Dialog();
                                        dialog.add(new Text("Veranstaltungsschema erfolgreich gespeichert"));
                                        UI.getCurrent().navigate(ManageLecturesView.ROUTE);
                                        dialog.open();
                                } finally {
                                        // TODO: implement ErrorHandeling
                                }

                        } else {
                                BinderValidationStatus<Lecture> validate = binder.validate();
                                String errorText = validate.getFieldValidationStatuses().stream()
                                                .filter(BindingValidationStatus::isError)
                                                .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                                                .collect(Collectors.joining(", "));
                                LOGGER.debug("There are errors: " + errorText);
                        }
                });
        }

        @Override
        public void setParameter(BeforeEvent event, String lectureId) {
                if (lectureId.equals("new")) {
                        objectId = null;
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {
                        Lecture fetchedLecture = lectureService.getById(lectureId);
                        /* getById returns null if no matching Lecture is found */
                        if (fetchedLecture == null) {
                                throw new NotFoundException();
                        } else {
                                objectId = fetchedLecture.getId();
                                binder.readBean(fetchedLecture);
                        }
                }
        }

}