package de.bp2019.pusl.ui.views.lecture;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.bson.types.ObjectId;
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.service.dataproviders.HiwiDataProvider;
import de.bp2019.pusl.ui.components.ExerciseComposer;
import de.bp2019.pusl.ui.components.PerformanceSchemeComposer;
import de.bp2019.pusl.ui.components.VerticalTabs;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * View containing a form to edit a Lecture
 * 
 * @author Leon Chemnitz
 */
@PageTitle(PuslProperties.NAME + " | Veranstaltung bearbeiten")
@Route(value = EditLectureView.ROUTE, layout = MainAppView.class)
public class EditLectureView extends BaseView implements HasUrlParameter<String>, AccessibleByAdmin {

        private static final long serialVersionUID = -7352842685521794385L;

        public static final String ROUTE = "admin/lecture";

        private InstituteService instituteService;
        private LectureService lectureService;
        private UserService userService;
        private ExerciseSchemeService exerciseSchemeService;
        private HiwiDataProvider hiwiDataProvider;

        /** Binder to bind the form Data to an Object */
        private Binder<Lecture> binder;

        /** empty if new institute is being created */
        private Optional<ObjectId> lectureId = Optional.empty();

        public EditLectureView() {
                super("Veranstaltung bearbeiten");

                this.instituteService = Service.get(InstituteService.class);
                this.lectureService = Service.get(LectureService.class);
                this.userService = Service.get(UserService.class);
                this.exerciseSchemeService = Service.get(ExerciseSchemeService.class);
                this.hiwiDataProvider = Service.get(HiwiDataProvider.class);

                FormLayout formLayout = new FormLayout();
                formLayout.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
                formLayout.setWidth("calc(100% - 1em)");
                formLayout.getStyle().set("marginLeft", "1em");
                formLayout.getStyle().set("marginTop", "-0.5em");

                binder = new Binder<>();

                /* ########### Create the fields ########### */

                TextField name = new TextField();
                name.setLabel("Name");
                name.setId("lecture-name");
                name.setPlaceholder("Name der Veranstaltung");
                name.setValueChangeMode(ValueChangeMode.EAGER);
                formLayout.add(name, 1);

                MultiselectComboBox<Institute> institutes = new MultiselectComboBox<>();
                institutes.setLabel("Institute");
                institutes.setId("lecture-institutes");
                institutes.setDataProvider(instituteService);
                institutes.setItemLabelGenerator(Institute::getName);
                formLayout.add(institutes, 1);

                VerticalTabs verticalTabs = new VerticalTabs();
                verticalTabs.setId("vtabs");
                verticalTabs.setHeight("25em");
                verticalTabs.setWidth("100%");

                ExerciseComposer exercises = new ExerciseComposer(exerciseSchemeService);
                verticalTabs.addTab("Prüfungen", exercises);

                PerformanceSchemeComposer performanceSchemes = new PerformanceSchemeComposer();
                verticalTabs.addTab("Leistungen", performanceSchemes);

                MultiselectComboBox<User> hasAccess = new MultiselectComboBox<>();
                hasAccess.setWidth("100%");
                hasAccess.setHeight("10em");
                hasAccess.setLabel("Zugriff");
                hasAccess.setDataProvider(hiwiDataProvider);
                hasAccess.setItemLabelGenerator(User::getFullName);
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

                binder.bind(hasAccess, lecture -> userService.getByIds(lecture.getHasAccess()), (lecture, items) -> {
                        lecture.setHasAccess(items.stream().map(u -> u.getId()).collect(Collectors.toSet()));
                });

                binder.bind(exercises, Lecture::getExercises, Lecture::setExercises);

                binder.bind(performanceSchemes, Lecture::getPerformanceSchemes, Lecture::setPerformanceSchemes);

                /* ########### Listeners ########### */

                exercises.addValueChangeListener(event -> LOGGER.debug("exercises changed " + event.getValue().size()));
                performanceSchemes.addValueChangeListener(event -> LOGGER.debug("performanceSchemes changed " + event.getValue().size()));

                institutes.addValueChangeListener(event -> {
                        hiwiDataProvider.setFilter(event.getValue());
                        hiwiDataProvider.refreshAll();
                });

                save.addClickListener(event -> {

                        if (!lectureService.checkNameAvailable(name.getValue(), lectureId)) {
                                ErrorDialog.open("Name bereits vergeben");
                                return;
                        }

                        Lecture lecture = new Lecture();

                        if (binder.writeBeanIfValid(lecture)) {
                                if (lectureId.isPresent()) {
                                        lecture.setId(lectureId.get());
                                }
                                try {
                                        lectureService.save(lecture);
                                        UI.getCurrent().navigate(ManageLecturesView.ROUTE);
                                        SuccessDialog.open("Veranstaltung erfolgreich gespeichert");
                                } catch (UnauthorizedException e) {
                                        ErrorDialog.open("nicht authorisiert um Veranstaltung zu speichern!");
                                }
                        } else {
                                BinderValidationStatus<Lecture> validate = binder.validate();
                                String errorText = validate.getFieldValidationStatuses().stream()
                                                .filter(BindingValidationStatus::isError)
                                                .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                                                .collect(Collectors.joining(", "));
                                LOGGER.info("Lecture could not be saved because of validation errors. Errors were: "
                                                + errorText);
                        }
                });
        }

        @Override
        public void setParameter(BeforeEvent event, String idParameter) {
                if (idParameter.equals("new")) {
                        lectureId = Optional.empty();
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {
                        try {
                                Lecture fetchedLecture;
                                fetchedLecture = lectureService.getById(idParameter);
                                lectureId = Optional.of(fetchedLecture.getId());
                                binder.readBean(fetchedLecture);
                        } catch (UnauthorizedException e) {
                                event.rerouteTo(PuslProperties.ROOT_ROUTE);
                                UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
                                ErrorDialog.open("Nicht authorisiert um Veranstaltung zu bearbeiten!");
                        } catch (DataNotFoundException e) {
                                event.rerouteTo(PuslProperties.ROOT_ROUTE);
                                UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
                                ErrorDialog.open("Veranstaltung nicht in Datenbank gefunden!");
                        }
                }
        }

}