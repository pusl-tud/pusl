package de.bp2019.zentraldatei.view.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;

/**
 * Component used as a Field to add, remove and arrange ExerciseSchemes in a
 * list. Used in ModuleSchemeView.
 * 
 * @author Leon Chemnitz
 */
public class ExerciseSchemeArranger extends CustomField<List<String>> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseSchemeArranger.class);

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
        exerciseSchemesGrid.setWidth("100%");
        exerciseSchemesGrid.setItems(gridItems);
        exerciseSchemesGrid.addColumn(item -> gridItems.indexOf(item) + 1).setWidth("1em");
        exerciseSchemesGrid.addColumn(ExerciseScheme::getName).setAutoWidth(true);
        exerciseSchemesGrid.addComponentColumn(item -> createDeleteButton(item));
        exerciseSchemesGrid.setSelectionMode(SelectionMode.NONE);
        exerciseSchemesGrid.setRowsDraggable(true);
        exerciseSchemesGrid.setHeight("15em");
        add(exerciseSchemesGrid);

        FormLayout newExerciseSchemeLayout = new FormLayout();
        newExerciseSchemeLayout.setWidthFull();
        setWidthFull();
        newExerciseSchemeLayout.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));

        Select<ExerciseScheme> exerciseSchemesSelect = new Select<>();
        exerciseSchemesSelect.setItemLabelGenerator(item -> item.getName());
        List<ExerciseScheme> allExerciseSchemes = exerciseSchemeService.getAllExerciseSchemes();
        exerciseSchemesSelect.setItems(allExerciseSchemes);
        exerciseSchemesSelect.setValue(allExerciseSchemes.get(0));
        newExerciseSchemeLayout.add(exerciseSchemesSelect);

        Button exerciseSchemesButton = new Button("Prüfungsschema hinzufügen");
        exerciseSchemesButton.addClickListener(event -> {
            gridItems.add(exerciseSchemesSelect.getValue().copy());
            exerciseSchemesGrid.getDataProvider().refreshAll();

            /* VERY INEFFICIENT!! BETTER SOLUTION NEEDED */
            setValue(gridItems.stream().map(ExerciseScheme::getId).collect(Collectors.toList()));
        });

        newExerciseSchemeLayout.add(exerciseSchemesButton);

        add(newExerciseSchemeLayout);

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
                exerciseSchemesGrid.getDataProvider().refreshAll();

                /* VERY INEFFICIENT!! BETTER SOLUTION NEEDED */
                setValue(gridItems.stream().map(ExerciseScheme::getId).collect(Collectors.toList()));
            }
        });
    }

    @Override
    protected List<String> generateModelValue() {
        LOGGER.info(gridItems.toString());
        return gridItems.stream().map(ExerciseScheme::getId).collect(Collectors.toList());
    }

    @Override
    protected void setPresentationValue(List<String> newPresentationValue) {
        gridItems.clear();
        newPresentationValue.stream().forEach(id -> gridItems.add(exerciseSchemeService.getExerciseSchemeById(id)));

        exerciseSchemesGrid.getDataProvider().refreshAll();
    }

    /**
     * Used to create the delete buttons for the Grid items.
     * 
     * @author Leon Chemnitz
     */
    private Button createDeleteButton(ExerciseScheme item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            gridItems.remove(item);
            exerciseSchemesGrid.getDataProvider().refreshAll();

            /* VERY INEFFICIENT!! BETTER SOLUTION NEEDED */
            setValue(gridItems.stream().map(ExerciseScheme::getId).collect(Collectors.toList()));
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return button;
    }
}