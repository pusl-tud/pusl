package de.bp2019.pusl.ui.views;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.DynamicFileDownloader;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.service.dataproviders.FilteringGradeDataProvider;
import de.bp2019.pusl.ui.components.VerticalTabs;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.util.ExcelExporter;

/**
 * View that displays all Grades and contains a form to add New Grades
 *
 * @author Luca Dinies
 **/

@PageTitle(PuslProperties.NAME + " | Noten")
@Route(value = WorkView.ROUTE, layout = MainAppView.class)
public class WorkView extends BaseView implements HasUrlParameter<String> {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "grades";

    private ListDataProvider<Lecture> lectureDataProvider;
    private FilteringGradeDataProvider filteringGradeDataProvider;

    private ObjectId objectId;

    private Binder<Grade> binder;

    private Select<Lecture> lectureFilter;
    private Select<Exercise> exerciseFilter;
    private TextField martrNumberFilter;

    Grid<Grade> grid;

    private Map<String, List<String>> parametersMap;

    /**
     * Filter for the Database Query, lookup Spring Data Query by Example!
     */
    private Grade filter;

    @Autowired
    public WorkView(GradeService gradeService, LectureService lectureService, UserService userService,
            FilteringGradeDataProvider filteringGradeDataProvider) {
        super("Noten eintragen");
        LOGGER.debug("Started creation of WorkView");

        this.filteringGradeDataProvider = filteringGradeDataProvider;

        List<Lecture> allLectures = new ArrayList<>();
        allLectures.add(new Lecture("Alle Anzeigen", null, null, null, null));
        allLectures.addAll(lectureService.fetch(new Query<>()).collect(Collectors.toList()));
        lectureDataProvider = new ListDataProvider<>(allLectures);

        filter = new Grade();

        Exercise filterCleanExercise = new Exercise("Alle Anzeigen", null, false);

        VerticalTabs verticalTabs = new VerticalTabs();
        verticalTabs.setHeight("100%");
        verticalTabs.setWidth("100%");

        VerticalLayout gridAndFilter = new VerticalLayout();

        /* ########### Create the filter Fields ########### */

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setWidth("100%");
        filterLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        VerticalLayout martrGradeLayout = new VerticalLayout();

        martrNumberFilter = new TextField();
        martrNumberFilter.setLabel("Matrikelnummer");
        martrNumberFilter.setPlaceholder("Matrikelnummer");
        martrNumberFilter.setValueChangeMode(ValueChangeMode.EAGER);
        martrGradeLayout.add(martrNumberFilter);

        TextField gradeFilter = new TextField();
        gradeFilter.setLabel("Note");
        gradeFilter.setPlaceholder("Note");
        gradeFilter.setValueChangeMode(ValueChangeMode.EAGER);
        martrGradeLayout.add(gradeFilter);

        filterLayout.add(martrGradeLayout);

        VerticalLayout moduleExerciseLayout = new VerticalLayout();

        lectureFilter = new Select<>();
        lectureFilter.setItemLabelGenerator(Lecture::getName);
        lectureFilter.setDataProvider(lectureDataProvider);
        lectureFilter.setLabel("Modul");
        moduleExerciseLayout.add(lectureFilter);

        exerciseFilter = new Select<>();
        exerciseFilter.setItemLabelGenerator(Exercise::getName);
        exerciseFilter.setEnabled(false);
        exerciseFilter.setLabel("Übung");

        exerciseFilter.setValue(filterCleanExercise);
        moduleExerciseLayout.add(exerciseFilter);

        filterLayout.add(moduleExerciseLayout);

        VerticalLayout dateLayout = new VerticalLayout();

        DatePicker startDateFilter = new DatePicker();
        startDateFilter.setLabel("Start");
        dateLayout.add(startDateFilter);

        DatePicker endDateFilter = new DatePicker();
        endDateFilter.setLabel("End");
        dateLayout.add(endDateFilter);

        filterLayout.add(dateLayout);

        gridAndFilter.add(filterLayout);

        /* ########### Create the Grid ########### */

        grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(filteringGradeDataProvider);

        grid.addColumn(Grade::getMatrNumber).setHeader("Matr. Nr.").setAutoWidth(true).setKey("matrNum");
        grid.addColumn(item -> item.getLecture().getName()).setHeader("Veranstaltung").setAutoWidth(true)
                .setKey("lecture");
        grid.addColumn(item -> item.getExercise().getName()).setHeader("Übung").setAutoWidth(true).setKey("exercise");
        grid.addColumn(item -> item.getHandIn()).setHeader("Abgabedatum").setAutoWidth(true).setKey("handin");
        grid.addColumn(item -> item.getGrade()).setHeader("Note").setAutoWidth(true).setKey("grade");

        gridAndFilter.add(grid);

        verticalTabs.addTab("Alle Noten", gridAndFilter);

        /* ############## Download Button ############# */

        DynamicFileDownloader downloadButton = new DynamicFileDownloader("Download als Excelliste", "Notenliste.xlsx",
                outputStream -> {
                    try {
                        ExcelExporter<Grade> excelExporter = new ExcelExporter<Grade>();

                        List<Grade> allGrades = filteringGradeDataProvider.fetch(new Query<>()).collect(Collectors.toList());
                        excelExporter.setItems(allGrades);
                        excelExporter.addColumn("Matr.Nummer", Grade::getMatrNumber);
                        excelExporter.addColumn("Note", Grade::getGrade);
                        excelExporter.addColumn("Veranstaltung", grade -> grade.getLecture().getName());
                        excelExporter.addColumn("Übung", grade -> grade.getExercise().getName());
                        excelExporter.addColumn("eingetragen von", grade -> {
                            // TODO : nullabilität entfernen
                            if (grade.getGradedBy() == null) {
                                return "";
                            } else {
                                return UserService.getFullName(grade.getGradedBy());
                            }
                        });
                        excelExporter.addColumn("Datum", grade -> {
                            // TODO : nullabilität entfernen
                            if (grade.getLastModified() == null) {
                                return "";
                            } else {
                                return grade.getLastModified().format(DateTimeFormatter.RFC_1123_DATE_TIME);
                            }
                        });

                        excelExporter.write(outputStream);
                    } catch (IOException e) {
                        ErrorDialog.open("Fehler beim Erstellen der Datei");
                        LOGGER.error(e.toString());
                    }
                });

        gridAndFilter.add(downloadButton);

        /* ############## CHANGE LISTENERS ############# */

        martrNumberFilter.addValueChangeListener(event -> {
            filter.setMatrNumber(event.getValue());
            reloadFilter();
        });

        gradeFilter.addValueChangeListener(event -> {
            filter.setGrade(event.getValue());
            reloadFilter();
        });

        lectureFilter.addValueChangeListener(event -> {
            if (event.getValue().getId() == null) {
                filter.setLecture(null);
                reloadFilter();
                grid.getColumnByKey("lecture").setVisible(true);
                grid.getColumnByKey("exercise").setVisible(true);

                exerciseFilter.setValue(filterCleanExercise);
                exerciseFilter.setEnabled(false);
            } else {
                filter.setLecture(event.getValue());
                reloadFilter();

                grid.getColumnByKey("lecture").setVisible(false);
                grid.getColumnByKey("exercise").setVisible(true);
                exerciseFilter.setValue(filterCleanExercise);

                List<Exercise> lectureExercises = event.getValue().getExercises();

                exerciseFilter.setItems(lectureExercises);
                exerciseFilter.setEnabled(true);

            }

        });

        exerciseFilter.addValueChangeListener(event -> {
            if (!exerciseFilter.isEmpty()) {
                if (exerciseFilter.getValue().getName().contains("Alle Anzeigen")) {
                    filter.setExercise(null);
                    reloadFilter();
                } else {
                    filter.setExercise(event.getValue());
                    reloadFilter();
                    grid.getColumnByKey("exercise").setVisible(false);
                }
            }
        });

        startDateFilter.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            LocalDate endDate = endDateFilter.getValue();
            if (selectedDate != null) {
                endDateFilter.setMin(selectedDate);
                //filteringGradeDataProvider.addFilter(grade -> grade.getHandIn().isAfter(startDateFilter.getValue()));
                if (endDate == null) {
                    endDateFilter.setOpened(true);
                }
            } else {
                endDateFilter.setMin(null);
            }
        });

        endDateFilter.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            LocalDate startDate = startDateFilter.getValue();
            if (selectedDate != null) {
                startDateFilter.setMax(selectedDate);
                //gradeDataProvider.addFilter(grade -> grade.getHandIn().isBefore(endDateFilter.getValue()));
                if (startDate == null) {
                    startDateFilter.setOpened(true);
                }
            } else {
                startDateFilter.setMax(null);
            }
        });

        /* ############## FORM TO INPUT A NEW GRADE ############# */

        binder = new Binder<>();

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("5em", 1), new FormLayout.ResponsiveStep("5em", 2));
        form.setWidth("100%");
        form.getStyle().set("marginLeft", "1em");
        form.getStyle().set("marginTop", "-0.5em");

        TextField matrikelNum = new TextField();
        matrikelNum.setPlaceholder("Matrikel Nummer");
        matrikelNum.setLabel("Matrikel Nummer");
        matrikelNum.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        form.add(matrikelNum);

        Select<Lecture> lectureSelect = new Select<>();
        lectureSelect.setItemLabelGenerator(Lecture::getName);
        lectureSelect.setDataProvider(lectureService);
        lectureSelect.setPlaceholder("Modul");
        lectureSelect.setLabel("Modul");
        form.add(lectureSelect);

        DatePicker datePicker = new DatePicker();
        datePicker.setLabel("Abgabe-Datum");
        datePicker.setValue(LocalDate.now());
        datePicker.setVisible(true);
        form.add(datePicker);

        Select<Exercise> exerciseSelect = new Select<>();
        exerciseSelect.setItemLabelGenerator(Exercise::getName);
        exerciseSelect.setEnabled(false);
        exerciseSelect.setLabel("Übung");
        form.add(exerciseSelect);

        TextField gradeField = new TextField();
        gradeField.setLabel("Note");
        gradeField.setPlaceholder("Note");
        gradeField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        form.add(gradeField);

        /* ########### Save Button and Layout ########### */

        Button save = new Button();
        save.setText("Speichern");

        VerticalLayout gradeInputLayout = new VerticalLayout();

        gradeInputLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, form);
        gradeInputLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, save);

        gradeInputLayout.add(form);
        gradeInputLayout.add(save);

        verticalTabs.addTab("Note eintragen", gradeInputLayout);

        add(verticalTabs);

        /* ########### Change Listeners for Selects ########### */

        lectureSelect.addValueChangeListener(event -> {
            Lecture selectedLecture = lectureSelect.getValue();
            if (event.getValue() != null) {
                List<Exercise> exercises = selectedLecture.getExercises();
                exerciseSelect.setItems(exercises);
                exerciseSelect.setEnabled(true);
                exerciseSelect.setValue(exercises.get(0));
            } else {
                exerciseSelect.setEnabled(false);
                exerciseSelect.setValue(null);
            }

        });

        /* ########### Click Listeners for Buttons ########### */

        save.addClickListener(event -> {
            Grade grade = new Grade();
            if (binder.writeBeanIfValid(grade)) {
                if (objectId != null) {
                    grade.setId(objectId);
                }
                grade.setLastModified(LocalDateTime.now());
                grade.setGradedBy(userService.currentUser());
                try {
                    gradeService.save(grade);
                } finally {
                    // TODO: implement ErrorHandling
                }
            }
            reloadFilter();
            matrikelNum.clear();
            exerciseSelect.clear();
            lectureSelect.clear();
            datePicker.setValue(LocalDate.now());
            gradeField.clear();
        });

        /* ########### Data Binding and validation ########### */

        // TODO: Validator
        binder.forField(matrikelNum).withValidator(new StringLengthValidator("Bitte Matrikelnummer eingeben", 1, null))
                .bind(Grade::getMatrNumber, Grade::setMatrNumber);

        binder.bind(lectureSelect, Grade::getLecture, Grade::setLecture);

        binder.bind(exerciseSelect, Grade::getExercise, Grade::setExercise);

        binder.bind(gradeField, Grade::getGrade, Grade::setGrade);

        binder.bind(datePicker, Grade::getHandIn, Grade::setHandIn);

    }

    /**
     * Fetch new Data from database, that matches the Filter
     *
     * @author Leon Chemnitz
     */
    private void reloadFilter() {
        LOGGER.debug(filter.toString());
        filteringGradeDataProvider.refreshAll();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        parametersMap = queryParameters.getParameters();

        if (parametersMap.get("lecture") != null) {
            String lectureId = parametersMap.get("lecture").get(0);
            Optional<Lecture> parameterLecture = lectureDataProvider.getItems().stream()
                    .filter(lecture -> lecture.getId() != null)
                    .filter(lecture -> lecture.getId().equals(new ObjectId(lectureId))).findFirst();

            if (parameterLecture.isPresent()) {
                lectureFilter.setValue(parameterLecture.get());
                lectureFilter.setPlaceholder(parameterLecture.get().getName());

                if (parametersMap.get("exercise") != null) {
                    String parameterExerciseName = parametersMap.get("exercise").get(0);
                    Optional<Exercise> parameterExercise = parameterLecture.get().getExercises().stream()
                            .filter(exercise -> exercise.getName().equals(parameterExerciseName)).findFirst();

                    if (parameterExercise.isPresent()) {
                        exerciseFilter.setValue(parameterExercise.get());
                    }
                }
            }

        }

        if (parametersMap.get("martrNumber") != null) {
            String parameterMartrikelNumber = parametersMap.get("martrNumber").get(0);
            martrNumberFilter.setValue(parameterMartrikelNumber);
        }

    }
}
