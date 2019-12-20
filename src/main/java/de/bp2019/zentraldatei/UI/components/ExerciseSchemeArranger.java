package de.bp2019.zentraldatei.UI.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import de.bp2019.zentraldatei.model.exercise.ExerciseScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.UI.views.Module.EditModuleView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Component used as a Field to add, remove and arrange {@link ExerciseScheme}s
 * in a list. Used in {@link EditModuleView}.
 * 
 * @author Leon Chemnitz
 */
public class ExerciseSchemeArranger extends CustomField<List<ExerciseScheme>> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseSchemeArranger.class);

    private static final String WIDTH = "15em";

    private Grid<ExerciseScheme> exerciseSchemesGrid;
    private ExerciseScheme draggedItem;
    private List<ExerciseScheme> gridItems;

    ExerciseSchemeService exerciseSchemeService;

    public ExerciseSchemeArranger(ExerciseSchemeService exerciseSchemeService) {
        Label label = new Label("Prüfungsschemas");
        add(label);
        this.exerciseSchemeService = exerciseSchemeService;

        exerciseSchemesGrid = new Grid<>();
        gridItems = new ArrayList<ExerciseScheme>();
        exerciseSchemesGrid.setItems(gridItems);
        exerciseSchemesGrid.addColumn(item -> gridItems.indexOf(item) + 1).setWidth("1em");
        exerciseSchemesGrid.addColumn(ExerciseScheme::getName).setWidth("5.3em");
        exerciseSchemesGrid.addComponentColumn(item -> createDeleteButton(item)).setWidth("0.7em");
        exerciseSchemesGrid.setSelectionMode(SelectionMode.NONE);
        exerciseSchemesGrid.setRowsDraggable(true);
        exerciseSchemesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        exerciseSchemesGrid.setHeight("15em");
        exerciseSchemesGrid.setWidth(WIDTH);
        add(exerciseSchemesGrid);

        Select<ExerciseScheme> exerciseSchemesSelect = new Select<>();
        exerciseSchemesSelect.setItemLabelGenerator(ExerciseScheme::getName);
        List<ExerciseScheme> allExerciseSchemes = exerciseSchemeService.getAllExerciseSchemes();
        exerciseSchemesSelect.setItems(allExerciseSchemes);
        exerciseSchemesSelect.setValue(allExerciseSchemes.get(0));
        exerciseSchemesSelect.setWidth(WIDTH);
        add(exerciseSchemesSelect);

        Button exerciseSchemesButton = new Button("Prüfungsschema hinzufügen", new Icon(VaadinIcon.PLUS_CIRCLE));
        exerciseSchemesButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        exerciseSchemesButton.setWidth(WIDTH);
        exerciseSchemesButton.addClickListener(event -> {
            gridItems.add(exerciseSchemesSelect.getValue().copy());
            setValue(new ArrayList<>(gridItems));
        });

        add(exerciseSchemesButton);

        /* ########### Drag and Drop Logic ########### */

        exerciseSchemesGrid.addDragStartListener(event -> {
            draggedItem = event.getDraggedItems().get(0);
            exerciseSchemesGrid.setDropMode(GridDropMode.BETWEEN);
        });

        exerciseSchemesGrid.addDragEndListener(event -> {
            draggedItem = null;
            exerciseSchemesGrid.setDropMode(null);
        });

        exerciseSchemesGrid.addDropListener(event -> {
            ExerciseScheme dropOverItem = event.getDropTargetItem().get();
            if (!dropOverItem.equals(draggedItem)) {
                gridItems.remove(draggedItem);
                int dropIndex = gridItems.indexOf(dropOverItem)
                        + (event.getDropLocation() == GridDropLocation.BELOW ? 1 : 0);
                gridItems.add(dropIndex, draggedItem);
                setValue(new ArrayList<>(gridItems));
            }
        });
    }

    @Override
    protected List<ExerciseScheme> generateModelValue() {
        LOGGER.info(gridItems.toString());
        return gridItems;
    }

    @Override
    protected void setPresentationValue(List<ExerciseScheme> newPresentationValue) {
        gridItems.clear();
        newPresentationValue.forEach(item -> gridItems.add(item.copy()));
        exerciseSchemesGrid.getDataProvider().refreshAll();
    }

    /**
     * Used to create the delete buttons for the Grid items.
     * 
     * @param item
     * @return
     * @author Leon Chemnitz
     */
    private Button createDeleteButton(ExerciseScheme item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            gridItems.remove(item);
            setValue(new ArrayList<>(gridItems));
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return button;
    }
}