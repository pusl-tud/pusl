package de.bp2019.zentraldatei.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.zentraldatei.model.Exercise;
import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.ui.views.module.EditModuleView;

/**
 * Component used as a Field to add, remove and arrange {@link Exercise}s in a
 * list. Used in {@link EditModuleView}.
 * 
 * @author Leon Chemnitz
 */
public class ExerciseComposer extends CustomField<List<Exercise>> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseComposer.class);

    private static final String WIDTH = "35em";

    private Grid<Exercise> grid;
    private Exercise draggedItem;
    private List<Exercise> gridItems;

    private TextField nameTextField;
    private Select<ExerciseScheme> exerciseSchemeSelect;

    private List<ExerciseScheme> allExerciseSchemes;

    public ExerciseComposer(ExerciseSchemeService exerciseSchemeService) {
        Label label = new Label("Prüfungen");
        add(label);

        grid = new Grid<>();
        gridItems = new ArrayList<>();
        grid.setItems(gridItems);
        grid.addColumn(item -> gridItems.indexOf(item) + 1).setAutoWidth(true);
        grid.addComponentColumn(item -> createNameLabel(item)).setAutoWidth(true);
        grid.addColumn(item -> item.getScheme().getName()).setAutoWidth(true);
        grid.addComponentColumn(item -> createHiWiAccessCheckbox(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setAutoWidth(true);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setRowsDraggable(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeight("20em");
        grid.setWidth(WIDTH);
        add(grid);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("1em", 1), new ResponsiveStep("1em", 2));

        nameTextField = new TextField();
        nameTextField.setPlaceholder("Prüfung");
        nameTextField.setValueChangeMode(ValueChangeMode.EAGER);
        formLayout.add(nameTextField, 1);

        exerciseSchemeSelect = new Select<>();
        exerciseSchemeSelect.setItemLabelGenerator(ExerciseScheme::getName);
        allExerciseSchemes = exerciseSchemeService.getAllExerciseSchemes();
        exerciseSchemeSelect.setItems(allExerciseSchemes);
        exerciseSchemeSelect.setValue(allExerciseSchemes.get(0));
        formLayout.add(exerciseSchemeSelect, 1);

        add(formLayout);

        Button exerciseSchemesButton = new Button("Prüfung hinzufügen", new Icon(VaadinIcon.PLUS_CIRCLE));
        exerciseSchemesButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        exerciseSchemesButton.setWidth(WIDTH);
        exerciseSchemesButton.addClickListener(event -> {
            Exercise exercise = new Exercise(nameTextField.getValue(), exerciseSchemeSelect.getValue(), false);
            gridItems.add(exercise);
            setValue(new ArrayList<>(gridItems));
        });

        add(exerciseSchemesButton);

        /* ########### Drag and Drop Logic ########### */

        grid.addDragStartListener(event -> {
            draggedItem = event.getDraggedItems().get(0);
            grid.setDropMode(GridDropMode.BETWEEN);
        });

        grid.addDragEndListener(event -> {
            draggedItem = null;
            grid.setDropMode(null);
        });

        grid.addDropListener(event -> {
            Exercise dropOverItem = event.getDropTargetItem().get();
            if (!dropOverItem.equals(draggedItem)) {
                gridItems.remove(draggedItem);
                int dropIndex = gridItems.indexOf(dropOverItem)
                        + (event.getDropLocation() == GridDropLocation.BELOW ? 1 : 0);
                gridItems.add(dropIndex, draggedItem);
                setValue(new ArrayList<>(gridItems));
            }
        });

        /* ########### Grid edit Logic ########### */

        grid.asSingleSelect().addValueChangeListener(event -> {
            nameTextField.setValue(event.getValue().getName());
            ExerciseScheme exerciseScheme = allExerciseSchemes.stream()
                    .filter(es -> es.getId().equals(event.getValue().getScheme().getId())).findFirst().get();
            exerciseSchemeSelect.setValue(exerciseScheme);
        });

        nameTextField.addValueChangeListener(event -> {
            Exercise exercise = grid.asSingleSelect().getValue();

            if (exercise != null) {
                exercise.setName(event.getValue());
                grid.asSingleSelect().setValue(exercise);
                grid.getDataProvider().refreshItem(exercise);
            }
        });

        exerciseSchemeSelect.addValueChangeListener(event -> {
            Exercise exercise = grid.asSingleSelect().getValue();

            if(exercise != null) {
                exercise.setScheme(event.getValue());
                grid.asSingleSelect().setValue(exercise);
                grid.getDataProvider().refreshItem(exercise);
            }
        });
    }

    @Override
    protected List<Exercise> generateModelValue() {
        LOGGER.info(gridItems.toString());
        return gridItems;
    }

    @Override
    protected void setPresentationValue(List<Exercise> newPresentationValue) {
        gridItems.clear();
        newPresentationValue.forEach(item -> gridItems.add(item));
        grid.getDataProvider().refreshAll();
    }

    /**
     * Used to create the delete buttons for the Grid items.
     * 
     * @param item
     * @return
     * @author Leon Chemnitz
     */
    private Button createDeleteButton(Exercise item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            gridItems.remove(item);
            setValue(new ArrayList<>(gridItems));
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return button;
    }

    private Label createNameLabel(Exercise item) {
        Label label = new Label(item.getName());
        label.getStyle().set("font-weight", "bold");
        return label;
    }

    private Checkbox createHiWiAccessCheckbox(Exercise item) {
        Checkbox checkbox = new Checkbox("HiWi", event -> {
            item.setAssignableByHIWI(event.getValue());
        });
        checkbox.setValue(item.isAssignableByHIWI());
        return checkbox;
    }
}