package de.bp2019.zentraldatei.UI.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bp2019.zentraldatei.UI.views.ExerciseScheme.EditExerciseSchemeView;
import de.bp2019.zentraldatei.model.exercise.Grade;
import de.bp2019.zentraldatei.service.GradeService;
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

    Grid<Grade> grid = new Grid<>();

    @Autowired
    public WorkView(GradeService gradeService){
        super("Noten eintragen");
        LOGGER.debug("Started creation of WorkView");

        this.gradeService = gradeService;

        grid.setWidth("100%");
        grid.setItems(gradeService.getAllGrades());

        grid.addColumn(Grade::getMatrNumber);
        grid.addColumn(item -> item.getModule().getName());
        grid.addColumn(item -> item.getExercise().getName());
        grid.addColumn(item -> item.getGrade());

        add(grid);
    }

}
