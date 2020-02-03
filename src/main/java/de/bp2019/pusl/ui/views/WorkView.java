package de.bp2019.pusl.ui.views;

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
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.ui.components.NoFlexExerciseDialog;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Luca Dinies
 *
 **/

@PageTitle(AppConfig.NAME + " | Noten eintragen")
@Route(value = WorkView.ROUTE, layout = MainAppView.class)
public class WorkView extends BaseView {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "grades";

    private ListDataProvider<Grade> gradeDataProvider;

    private GradeService gradeService;

    /** Filter for the Database Query, lookup Spring Data Query by Example! */
    private Grade filter;

    @Autowired
    public WorkView(GradeService gradeService, ExerciseSchemeService exerciseSchemeService,
                    LectureService lectureService) {
        super("Noten eintragen");
        LOGGER.debug("Started creation of WorkView");


        this.gradeService = gradeService;

        gradeDataProvider = new ListDataProvider<Grade>(gradeService.getAll());

        filter = new Grade();;

        Lecture filterCleanModule = new Lecture("Alle Anzeigen", null, null, null, null);
        Exercise filterCleanExercise = new Exercise("Alle Anzeigen", null, false);

        /* ########### Create the filter Fields ########### */

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setWidth("100%");
        filterLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        VerticalLayout martrGradeLayout = new VerticalLayout();

        TextField martrNumber = new TextField();
        martrNumber.setLabel("Matrikelnummer");
        martrNumber.setPlaceholder("Matrikelnummer");
        martrNumber.setValueChangeMode(ValueChangeMode.EAGER);
        martrGradeLayout.add(martrNumber);

        TextField gradeFilter = new TextField();
        gradeFilter.setLabel("Note");
        gradeFilter.setPlaceholder("Note");
        gradeFilter.setValueChangeMode(ValueChangeMode.EAGER);
        martrGradeLayout.add(gradeFilter);

        filterLayout.add(martrGradeLayout);

        VerticalLayout moduleExerciseLayout = new VerticalLayout();

        Select<Lecture> lectureFilter = new Select<>();
        lectureFilter.setItemLabelGenerator(Lecture::getName);
        List<Lecture> allLectures = lectureService.getAll();
        allLectures.add(0, filterCleanModule);
        lectureFilter.setItems(allLectures);
        lectureFilter.setValue(allLectures.get(0));
        lectureFilter.setLabel("Modul");
        moduleExerciseLayout.add(lectureFilter);

        Select<Exercise> exerciseFilter = new Select<>();
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

        add(filterLayout);

        /* ########### Create the Grid ########### */

        Grid<Grade> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(gradeDataProvider);

        grid.addColumn(Grade::getMatrNumber).setHeader("Matr. Nr.").setAutoWidth(true).setKey("matrikelNum");
        grid.addColumn(item -> item.getLecture().getName()).setHeader("Veranstaltung").setAutoWidth(true).setKey("lecture");
        grid.addColumn(item -> item.getExercise().getName()).setHeader("Übung").setAutoWidth(true).setKey("exercise");
        grid.addColumn(item -> item.getHandIn()).setHeader("Abgabedatum").setAutoWidth(true).setKey("handin");
        grid.addColumn(item -> item.getGrade()).setHeader("Note").setAutoWidth(true).setKey("grade");
        add(grid);

        Button exerciseHandin = new Button("Übung eingeben");
        exerciseHandin.addClickListener(event -> {
            NoFlexExerciseDialog exerciseWindow = new NoFlexExerciseDialog(lectureService, gradeService);
        });

        add(exerciseHandin);

        /*############## CHANGE LISTENERS ############# */

        martrNumber.addValueChangeListener(event -> {
            filter.setMatrNumber(event.getValue());
            reloadFilter();
        });

        gradeFilter.addValueChangeListener(event -> {
            filter.setGrade(event.getValue());
            reloadFilter();
        });

        lectureFilter.addValueChangeListener(event -> {
            if(event.getValue().getId() == null){
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
                exerciseFilter.setValue(filterCleanExercise);

                List<Exercise> lectureExercises = event.getValue().getExercises();

                exerciseFilter.setItems(lectureExercises);
                exerciseFilter.setEnabled(true);
            }
        });

        exerciseFilter.addValueChangeListener(event -> {
            if(!exerciseFilter.isEmpty()) {
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
            if(selectedDate != null){
                endDateFilter.setMin(selectedDate);
                gradeDataProvider.addFilter(grade -> grade.getHandIn().isAfter(startDateFilter.getValue()));
                if(endDate == null){
                    endDateFilter.setOpened(true);
                }
            } else {
                endDateFilter.setMin(null);
            }
        });

        endDateFilter.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            LocalDate startDate = startDateFilter.getValue();
            if(selectedDate != null){
                startDateFilter.setMax(selectedDate);
                gradeDataProvider.addFilter(grade -> grade.getHandIn().isBefore(endDateFilter.getValue()));
                if(startDate == null){
                    startDateFilter.setOpened(true);
                }
            } else {
                startDateFilter.setMax(null);
            }
        });

    }

    /**
     * Fetch new Data from database, that matches the Filter
     *
     * @author Leon Chemnitz
     */
    private void reloadFilter() {
        LOGGER.debug(filter.toString());
        gradeDataProvider.getItems().clear();
        gradeDataProvider.getItems().addAll(gradeService.getAll(filter));
        gradeDataProvider.refreshAll();
    }
}
