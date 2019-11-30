package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.bp2019.zentraldatei.model.ExerciseScheme;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Luca Dinies
 *
 **/

public class ManageExerciseSchemesView extends VerticalLayout {

    public ManageExerciseSchemesView() {

        // Test Daten
        List<ExerciseScheme> exercises = Arrays.asList(

                new ExerciseScheme("RO Hausübung 2", true, null, null, null, null),
                new ExerciseScheme("RO Hausübung 1", true, null, null, null, null),
                new ExerciseScheme("RO Hausübung 3", true, null, null, null, null)

        );


        // Table for the exist Exercises
        Grid<ExerciseScheme> actualExercises = new Grid<>(ExerciseScheme.class);
        actualExercises.setItems(exercises);
        actualExercises.removeAllColumns();
        actualExercises.addColumn("name");

        add(actualExercises);

        Button createExercise =new Button("Veranstaltung erstellen");
        add(createExercise);






    }

}
