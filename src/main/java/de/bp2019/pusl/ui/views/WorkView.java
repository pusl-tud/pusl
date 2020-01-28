package de.bp2019.pusl.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.ui.components.NoFlexExerciseDialog;

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

        this.gradeService = gradeService;

        gradeDataProvider = new ListDataProvider<Grade>(gradeService.getAll());

        filter = new Grade();;

        /* ########### Create the Grid ########### */

        Grid<Grade> grid = new Grid<>();

        grid.setDataProvider(gradeDataProvider);

        TextField martrNumber = new TextField();
        martrNumber.setLabel("Matrikelnummer");
        martrNumber.setPlaceholder("Matrikelnummer");
        martrNumber.setValueChangeMode(ValueChangeMode.EAGER);
        add(martrNumber);

        grid.setWidth("100%");
        grid.setDataProvider(gradeDataProvider);

        grid.addColumn(Grade::getMatrNumber).setHeader("Matr. Nr.").setAutoWidth(true);
        grid.addColumn(item -> item.getLecture().getName()).setHeader("Veranstaltung").setAutoWidth(true);
        grid.addColumn(item -> item.getExercise().getName()).setHeader("Übung").setAutoWidth(true);
        grid.addColumn(item -> item.getHandIn()).setHeader("Abgabedatum").setAutoWidth(true);
        grid.addColumn(item -> item.getGrade()).setHeader("Note").setAutoWidth(true);
        add(grid);

        Button exerciseHandin = new Button("Übung eingeben");
        exerciseHandin.addClickListener(event -> {
            NoFlexExerciseDialog exerciseWindow = new NoFlexExerciseDialog(lectureService, exerciseSchemeService,
                    gradeService);
        });

        add(exerciseHandin);

        /*############## CHANGE LISTENERS ############# */

        martrNumber.addValueChangeListener(event -> {
            filter.setMatrNumber(event.getValue());
            reloadFilter();
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
