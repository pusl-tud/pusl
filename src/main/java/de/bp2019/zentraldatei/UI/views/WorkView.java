package de.bp2019.zentraldatei.UI.views;

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


import de.bp2019.zentraldatei.model.Exercise;
import de.bp2019.zentraldatei.model.Module;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.zentraldatei.model.Grade;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.GradeService;
import de.bp2019.zentraldatei.service.ModuleService;
import de.bp2019.zentraldatei.UI.components.NoFlexExerciseDialog;
import de.bp2019.zentraldatei.UI.views.ExerciseScheme.EditExerciseSchemeView;

/**
 * View containing a form to filter the {@link Grade}s in the Grid.
 *
 * @author Luca Dinies
 **/

@PageTitle("Zentraldatei | Noten eintragen")
@Route(value = WorkView.ROUTE, layout = MainAppView.class)
public class WorkView extends BaseView {

    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "edit-Grades";
    private static final Logger LOGGER = LoggerFactory.getLogger(EditExerciseSchemeView.class);

    public Grid<Grade> grid = new Grid<>();
    public TextField martrNumber;
    public Select<Module> moduleFilter;
    public Select<Exercise> exerciseFilter;
    public DatePicker startDateFilter;
    public DatePicker endDateFilter;

    public Module filterCleanModule;
    public Exercise filterCleanExercise;

    @Autowired
    public WorkView(GradeService gradeService, ExerciseSchemeService exerciseSchemeService,
            ModuleService moduleService) {
        super("Noten eintragen");
        LOGGER.debug("Started creation of WorkView");

        /* ########### Create the filter-fields ########### */

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setWidth("100%");
        filterLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        List<Grade> gradeList = gradeService.getAllGrades();

        ListDataProvider<Grade> dataProvider = new ListDataProvider<>(gradeList);
        grid.setDataProvider(dataProvider);

        martrNumber = new TextField();
        martrNumber.setLabel("Matrikelnummer");
        martrNumber.setPlaceholder("Matrikelnummer");
        martrNumber.setValueChangeMode(ValueChangeMode.EAGER);
        filterLayout.add(martrNumber);

        VerticalLayout moduleExerciseLayout = new VerticalLayout();

        filterCleanModule = new Module("Alle Anzeigen", null, null, null, null);
        filterCleanExercise = new Exercise("Alle Anzeigen", null, false);

        moduleFilter = new Select<>();
        moduleFilter.setItemLabelGenerator(Module::getName);
        List<Module> allModules = moduleService.getAllModules();
        allModules.add(0, filterCleanModule);
        moduleFilter.setItems(allModules);
        moduleFilter.setValue(allModules.get(0));
        moduleFilter.setLabel("Modul");
        moduleExerciseLayout.add(moduleFilter);

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
        grid.addColumn(item -> item.getModule().getName()).setHeader("Modul").setAutoWidth(true).setKey("modul");
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

        moduleFilter.addValueChangeListener(event -> {
            applyFilter(dataProvider);

            if(!moduleFilter.getValue().getName().equalsIgnoreCase("Alle Anzeigen")) {
                Module selectedModule = moduleFilter.getValue();
                List<Exercise> exercises = selectedModule.getExercises();
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

        if(!moduleFilter.isEmpty()) {
            if (!moduleFilter.getValue().getName().equalsIgnoreCase("Alle Anzeigen")) {
                dataProvider.addFilter(grade -> StringUtils.containsIgnoreCase(grade.getModule().getName(), moduleFilter.getValue().getName()));
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
