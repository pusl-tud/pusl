package de.bp2019.zentraldatei.ui.views;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import de.bp2019.zentraldatei.model.Grade;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.GradeService;
import de.bp2019.zentraldatei.service.ModuleService;
import de.bp2019.zentraldatei.ui.components.NoFlexExerciseDialog;
import de.bp2019.zentraldatei.ui.views.exercisescheme.EditExerciseSchemeView;

/**
 *
 * @author Luca Dinies
 *
 **/

@PageTitle("Zentraldatei | Noten eintragen")
@Route(value = WorkView.ROUTE, layout = MainAppView.class)
public class WorkView extends BaseView {

    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "edit-Grades";
    private static final Logger LOGGER = LoggerFactory.getLogger(EditExerciseSchemeView.class);

    public Grid<Grade> grid = new Grid<>();

    @Autowired
    public WorkView(GradeService gradeService, ExerciseSchemeService exerciseSchemeService,
            ModuleService moduleService) {
        super("Noten eintragen");
        LOGGER.debug("Started creation of WorkView");

        /* ########### Create the Grid ########### */

        List<Grade> gradeList = gradeService.getAllGrades();

        TextField martrNumber = new TextField();
        martrNumber.setLabel("Matrikelnummer");
        martrNumber.setPlaceholder("Matrikelnummer");
        martrNumber.setValueChangeMode(ValueChangeMode.EAGER);
        add(martrNumber);

        grid.setWidth("100%");
        grid.setItems(gradeList);
        grid.addColumn(Grade::getMatrNumber).setHeader("Matr. Nr.").setAutoWidth(true);
        grid.addColumn(item -> item.getModule().getName()).setHeader("Modul").setAutoWidth(true);
        grid.addColumn(item -> item.getExercise().getName()).setHeader("Übung").setAutoWidth(true);
        grid.addColumn(item -> item.getHandIn()).setHeader("Abgabedatum").setAutoWidth(true);
        grid.addColumn(item -> item.getGrade()).setHeader("Note").setAutoWidth(true);
        add(grid);

        Button exerciseHandin = new Button("Übung eingeben");
        exerciseHandin.addClickListener(event -> {
            NoFlexExerciseDialog exerciseWindow = new NoFlexExerciseDialog(moduleService, exerciseSchemeService,
                    gradeService);
        });

        martrNumber.addValueChangeListener(event -> {
            gradeList.clear();
            gradeList.addAll(gradeService.getAllGrades().stream()
                    .filter(grade -> String.valueOf(grade.getMatrNumber()).contains(event.getValue()))
                    .collect(Collectors.toList()));
            grid.getDataProvider().refreshAll();
        });

        add(exerciseHandin);
    }

}
