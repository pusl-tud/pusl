package de.bp2019.zentraldatei.UI.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bp2019.zentraldatei.UI.components.NoFlexExerciseDialog;
import de.bp2019.zentraldatei.UI.views.ExerciseScheme.EditExerciseSchemeView;
import de.bp2019.zentraldatei.model.exercise.ExerciseScheme;
import de.bp2019.zentraldatei.model.exercise.Grade;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.GradeService;
import de.bp2019.zentraldatei.service.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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

    private GradeService gradeService;
    private ExerciseSchemeService exerciseSchemeService;
    private ModuleService moduleService;

    public Grid<Grade> grid = new Grid<>();

    @Autowired
    public WorkView(GradeService gradeService, ExerciseSchemeService exerciseSchemeService, ModuleService moduleService){
        super("Noten eintragen");
        LOGGER.debug("Started creation of WorkView");

        this.gradeService = gradeService;
        this.exerciseSchemeService = exerciseSchemeService;
        this.moduleService = moduleService;


        /* ########### Create the Grid ########### */

        grid.setWidth("100%");
        grid.setItems(gradeService.getAllGrades());
        grid.addColumn(Grade::getMatrNumber).setHeader("Matrikelnummer");
        grid.addColumn(item -> item.getModule().getName()).setHeader("Modul");
        grid.addColumn(item -> item.getExercise().getName()).setHeader("Übung");
        grid.addColumn(item -> item.getHandIn()).setHeader("Abgabedatum");
        grid.addColumn(item -> item.getGrade()).setHeader("Note");
        add(grid);

        Button exerciseHandin = new Button("Übung eingeben");
        exerciseHandin.addClickListener(event -> {
            NoFlexExerciseDialog exerciseWindow = new NoFlexExerciseDialog(moduleService, exerciseSchemeService, gradeService);
        });



        add(exerciseHandin);
    }

}
