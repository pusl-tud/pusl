package de.bp2019.zentraldatei.UI.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bp2019.zentraldatei.UI.views.ExerciseScheme.EditExerciseSchemeView;
import de.bp2019.zentraldatei.model.exercise.Grade;
import de.bp2019.zentraldatei.service.ExerciseInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Luca Dinies
 *
 **/

@PageTitle("Zentraldatei | Noten eintragen")
@Route(value = WorkView.ROUTE, layout = MainAppView.class)
public class WorkView extends BaseView {

    private static final long serialVersionUID = 326845216973245687L;
    public static final String ROUTE = "edit-Grades";
    private static final Logger LOGGER = LoggerFactory.getLogger(EditExerciseSchemeView.class);
    private ExerciseInstanceService exerciseInstanceService;

    Grid<Grade> grid = new Grid<>();

    public WorkView(ExerciseInstanceService exerciseInstanceService){
        super("Noten eintragen");
        LOGGER.debug("Started creation of WorkView");

        this.exerciseInstanceService = exerciseInstanceService;

        grid.setWidth("100%");



    }
}
