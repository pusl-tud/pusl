package de.bp2019.pusl.ui.views;

import java.time.LocalDate;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Lecture;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.ui.components.NoFlexExerciseDialog;
import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;

/**
 * View containing a form to filter the {@link Grade}s in the Grid.
 *
 * @author Luca Dinies
 **/

@PageTitle("pusl | Noten eintragen")
@Route(value = WorkView.ROUTE, layout = MainAppView.class)
public class WorkView extends BaseView {

    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "edit-Grades";
    private static final Logger LOGGER = LoggerFactory.getLogger(EditExerciseSchemeView.class);

    public Grid<Grade> grid = new Grid<>();
    public TextField gradeFilter;
    public TextField martrNumber;
    public Select<Lecture> lectureFilter;
    public Select<Exercise> exerciseFilter;
    public DatePicker startDateFilter;
    public DatePicker endDateFilter;

    public Lecture filterCleanLecture;
    public Exercise filterCleanExercise;

    @Autowired
    public WorkView(GradeService gradeService, ExerciseSchemeService exerciseSchemeService,
                    LectureService moduleService) {
        super("Noten eintragen");
        LOGGER.debug("Started creation of WorkView");

        filterCleanLecture = new Lecture("Alle Anzeigen", null, null, null, null);
        filterCleanExercise = new Exercise("Alle Anzeigen", null, false);

        /* ########### Create the filter-fields ########### */

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setWidth("100%");
        filterLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        List<Grade> gradeList = gradeService.getAll();

        ListDataProvider<Grade> dataProvider = new ListDataProvider<>(gradeList);
        grid.setDataProvider(dataProvider);

        VerticalLayout martrGradeLayout = new VerticalLayout();

        martrNumber = new TextField();
        martrNumber.setLabel("Matrikelnummer");
        martrNumber.setPlaceholder("Matrikelnummer");
        martrNumber.setValueChangeMode(ValueChangeMode.EAGER);
        martrGradeLayout.add(martrNumber);

        gradeFilter = new TextField();
        gradeFilter.setLabel("Note");
        gradeFilter.setPlaceholder("Note");
        gradeFilter.setValueChangeMode(ValueChangeMode.EAGER);
        martrGradeLayout.add(gradeFilter);

        filterLayout.add(martrGradeLayout);

        VerticalLayout moduleExerciseLayout = new VerticalLayout();

        lectureFilter = new Select<>();
        lectureFilter.setItemLabelGenerator(Lecture::getName);
        List<Lecture> allLectures = moduleService.getAll();
        allLectures.add(0, filterCleanLecture);
        lectureFilter.setItems(allLectures);
        lectureFilter.setValue(allLectures.get(0));
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

        startDateFilter = new DatePicker();
        startDateFilter.setLabel("Start");
        dateLayout.add(startDateFilter);

        endDateFilter = new DatePicker();
        endDateFilter.setLabel("End");
        dateLayout.add(endDateFilter);

        filterLayout.add(dateLayout);

        add(filterLayout);

        /* ########### Create the Grid ########### */

        grid.setWidth("100%");
        grid.addColumn(Grade::getMatrNumber).setHeader("Matr. Nr.").setAutoWidth(true).setKey("matrikelNum");
        grid.addColumn(item -> item.getLecture().getName()).setHeader("Modul").setAutoWidth(true).setKey("modul");
        grid.addColumn(item -> item.getExercise().getName()).setHeader("Übung").setAutoWidth(true).setKey("exercise");
        grid.addColumn(item -> item.getHandIn()).setHeader("Abgabedatum").setAutoWidth(true).setKey("handin");
        grid.addColumn(item -> item.getGrade()).setHeader("Note").setAutoWidth(true).setKey("grade");
        add(grid);


        Button exerciseHandin = new Button("Übung eingeben");
        exerciseHandin.addClickListener(event -> {
            NoFlexExerciseDialog exerciseWindow = new NoFlexExerciseDialog(moduleService, gradeService);
        });

        add(exerciseHandin);

        /* ########### ChangeListeners for the filter fields ########### */

        martrNumber.addValueChangeListener(event -> {
            applyFilter(dataProvider);
        });

        gradeFilter.addValueChangeListener(event -> {
            applyFilter(dataProvider);
        });

        lectureFilter.addValueChangeListener(event -> {
            applyFilter(dataProvider);

            if(!lectureFilter.getValue().getName().equalsIgnoreCase("Alle Anzeigen")) {
                Lecture selectedLecture = lectureFilter.getValue();
                List<Exercise> exercises = selectedLecture.getExercises();
                if (!exercises.get(0).getName().contains("Alle Anzeigen")) {
                    exercises.add(0, filterCleanExercise);
                }
                exerciseFilter.setItems(exercises);
                exerciseFilter.setEnabled(true);
                exerciseFilter.setValue(filterCleanExercise);
            }
        });

        exerciseFilter.addValueChangeListener((event -> {
            applyFilter(dataProvider);
        }));

        startDateFilter.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            LocalDate endDate = endDateFilter.getValue();
            if(selectedDate != null){
                endDateFilter.setMin(selectedDate);
                if(endDate == null){
                    endDateFilter.setOpened(true);
                }
            }
            applyFilter(dataProvider);
        });

        endDateFilter.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            LocalDate startDate = startDateFilter.getValue();
            if(selectedDate != null){
                startDateFilter.setMax(selectedDate);
                if(startDate == null){
                    startDateFilter.setOpened(true);
                }
            }
            applyFilter(dataProvider);
        });
    }

    /* ########### Filter Logic ########### */

    public void applyFilter(ListDataProvider<Grade> dataProvider){
        dataProvider.clearFilters();

        if(!martrNumber.isEmpty()){
            dataProvider.addFilter(grade -> StringUtils.containsIgnoreCase(String.valueOf(grade.getMatrNumber()), martrNumber.getValue()));
        }

        if(!gradeFilter.isEmpty()){
            dataProvider.addFilter(grade -> StringUtils.containsIgnoreCase(grade.getGrade(), gradeFilter.getValue()));
        }

        if(!lectureFilter.isEmpty()) {
            if (!lectureFilter.getValue().getName().equalsIgnoreCase("Alle Anzeigen")) {
                dataProvider.addFilter(grade -> StringUtils.containsIgnoreCase(grade.getLecture().getName(), lectureFilter.getValue().getName()));
                grid.getColumnByKey("modul").setVisible(false);
            } else {
                grid.getColumnByKey("modul").setVisible(true);
                exerciseFilter.setValue(filterCleanExercise);
                exerciseFilter.setEnabled(false);
            }
        }

        if (!exerciseFilter.isEmpty()) {
            if (exerciseFilter.getValue().getName().contains("Alle Anzeigen")) {
                grid.getColumnByKey("exercise").setVisible(true);
            } else {
                dataProvider.addFilter(grade -> StringUtils.containsIgnoreCase(grade.getExercise().getName(), exerciseFilter.getValue().getName()));
                grid.getColumnByKey("exercise").setVisible(false);
            }
        }

        if(!startDateFilter.isEmpty()) {
            dataProvider.addFilter(grade -> grade.getHandIn().isAfter(startDateFilter.getValue().minusDays(1)));
        }

        if(!endDateFilter.isEmpty()){
            dataProvider.addFilter(grade -> grade.getHandIn().isBefore(endDateFilter.getValue().plusDays(1)));
        }

    }

}