package de.bp2019.pusl.ui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.ui.views.lecture.EditLectureView;

/**
 * Component used as a Field to add, remove and arrange {@link Exercise}s in a
 * list. Used in {@link EditLectureView}.
 * 
 * @author Leon Chemnitz
 */
public class ExerciseComposer extends CustomField<List<Exercise>> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseComposer.class);

    private Grid<Exercise> grid;
    private Exercise draggedItem;
    private List<Exercise> gridItems;

    private TextField nameTextField;
    private Select<ExerciseScheme> exerciseSchemeSelect;

    public ExerciseComposer(ExerciseSchemeService exerciseSchemeService) {
        setWidth("100%");
        grid = new Grid<>();
        gridItems = new ArrayList<>();
        grid.setItems(gridItems);
        grid.addColumn(item -> gridItems.indexOf(item) + 1).setAutoWidth(true);
        grid.addComponentColumn(this::createNameLabel).setAutoWidth(true);
        grid.addColumn(item -> item.getScheme().getName()).setAutoWidth(true);
        grid.addComponentColumn(this::createHiWiAccessCheckbox).setAutoWidth(true);
        grid.addComponentColumn(this::createDeleteButton).setAutoWidth(true);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setRowsDraggable(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeight("21em");
        grid.setWidth("100%");
        grid.getStyle().set("margin-top", "2em");
        add(grid);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("1em", 1), new ResponsiveStep("1em", 2),
                new ResponsiveStep("1em", 3));

        nameTextField = new TextField();
        nameTextField.setId("new-exercise-name");
        nameTextField.setPlaceholder("Prüfung");
        nameTextField.setValueChangeMode(ValueChangeMode.EAGER);
        formLayout.add(nameTextField, 1);

        exerciseSchemeSelect = new Select<>();
        exerciseSchemeSelect.setId("new-exercise-scheme");
        exerciseSchemeSelect.setItemLabelGenerator(ExerciseScheme::getName);
        exerciseSchemeSelect.setDataProvider(exerciseSchemeService);

        formLayout.add(exerciseSchemeSelect, 1);

        Button newExerciseButton = new Button("hinzufügen", new Icon(VaadinIcon.PLUS_CIRCLE));
        newExerciseButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        formLayout.add(newExerciseButton, 1);

        add(formLayout);

        /* ########### Drag and Drop Logic ########### */

        grid.addDragStartListener(event -> {
            draggedItem = event.getDraggedItems().get(0);
            if (draggedItem != null) {
                grid.setDropMode(GridDropMode.BETWEEN);
            }
        });

        grid.addDragEndListener(event -> {
            draggedItem = null;
            grid.setDropMode(null);
        });

        grid.addDropListener(event -> {
            if (event.getDropTargetItem().isPresent()) {
                Exercise dropOverItem = event.getDropTargetItem().get();
                if (!dropOverItem.equals(draggedItem)) {
                    gridItems.remove(draggedItem);
                    int dropIndex = gridItems.indexOf(dropOverItem)
                            + (event.getDropLocation() == GridDropLocation.BELOW ? 1 : 0);
                    gridItems.add(dropIndex, draggedItem);
                    grid.getDataProvider().refreshAll();
                    updateValue();
                }
            }
        });

        /* ########### Grid edit Logic ########### */

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                nameTextField.setValue(event.getValue().getName());
                ExerciseScheme exerciseScheme = exerciseSchemeService.fetch(new Query<>())
                        .filter(es -> es.getId().equals(event.getValue().getScheme().getId())).findFirst().get();
                exerciseSchemeSelect.setValue(exerciseScheme);
            }
        });

        nameTextField.addValueChangeListener(event -> {
            Exercise exercise = grid.asSingleSelect().getValue();

            if (exercise != null) {
                Optional<Exercise> changedExercise = gridItems.stream().filter(i -> i.getId().equals(exercise.getId()))
                        .findFirst();

                if (changedExercise.isPresent()) {
                    changedExercise.get().setName(event.getValue());
                    grid.getDataProvider().refreshAll();
                    updateValue();
                }
            }
        });

        exerciseSchemeSelect.addValueChangeListener(event -> {
            Exercise exercise = grid.asSingleSelect().getValue();

            if (exercise != null) {
                Optional<Exercise> changedExercise = gridItems.stream().filter(i -> i.getId().equals(exercise.getId()))
                        .findFirst();

                if (changedExercise.isPresent()) {
                    grid.getDataProvider().refreshAll();
                    changedExercise.get().setScheme(event.getValue());
                    grid.getDataProvider().refreshAll();
                    updateValue();
                }
            }
        });

        newExerciseButton.addClickListener(event -> {
            if (!nameTextField.getValue().equals("") && exerciseSchemeSelect.getValue() != null) {
                LOGGER.debug("added new Exercise");

                Exercise exercise = new Exercise(nameTextField.getValue(), exerciseSchemeSelect.getValue(), false);
                gridItems.add(exercise);
                grid.getDataProvider().refreshAll();
                updateValue();

                nameTextField.setValue("");
            }
        });

    }

    @Override
    protected List<Exercise> generateModelValue() {
        List<Exercise> modelValue = new ArrayList<>();
        gridItems.forEach(e -> modelValue.add(new Exercise(e)));
        return modelValue;
    }

    @Override
    protected void setPresentationValue(List<Exercise> newPresentationValue) {
        gridItems.clear();
        gridItems.addAll(newPresentationValue);
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
            grid.getDataProvider().refreshAll();
            updateValue();
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
        Checkbox checkbox = new Checkbox("HiWi");
        checkbox.setValue(item.isAssignableByHIWI());
        checkbox.addValueChangeListener(event -> {
            Optional<Exercise> changedExercise = gridItems.stream().filter(i -> i.getId().equals(item.getId()))
                    .findFirst();

            if (changedExercise.isPresent()) {
                changedExercise.get().setAssignableByHIWI(event.getValue());
                LOGGER.debug(
                        "Exercise " + item.getName() + " changed HIWI access to " + String.valueOf(event.getValue()));
                updateValue();
            }
        });
        return checkbox;
    }
}