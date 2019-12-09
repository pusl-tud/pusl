package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Luca Dinies
 *
 **/
@Route(value ="exerciseScheme", layout = MainAppView.class)
public class ManageExerciseSchemesView extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageExerciseSchemesView.class);

    private ExerciseSchemeService exerciseSchemeService;

	public ManageExerciseSchemesView(@Autowired ExerciseSchemeService exerciseSchemeService) {

        LOGGER.debug("started creation of ManageExerciseSchemesView");

        setWidth("50em");

        // Table for the exist Exercises
        Grid<ExerciseScheme> grid = new Grid<>(ExerciseScheme.class);
        grid.setWidth("100%");
        grid.setItems(exerciseSchemeService.getAllExerciseSchemes());

        add(grid);

        Button newExerciseSchemeButton = new Button("Neue Übung");
        newExerciseSchemeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newExerciseSchemeButton);
        setHorizontalComponentAlignment(Alignment.END, newExerciseSchemeButton);

        newExerciseSchemeButton.addClickListener(event -> UI.getCurrent().navigate("exerciseScheme/new"));

        LOGGER.debug("finished creation of ManageExerciseSchemesView");

    }

}
