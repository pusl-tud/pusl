package de.bp2019.pusl.ui.components;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;

import de.bp2019.pusl.ui.views.exercisescheme.EditExerciseSchemeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.service.ExerciseSchemeService;

/**
 * Component used as a Field to add and remove {@link Token}s in a list. Used in
 * {@link EditExerciseSchemeView}.
 *
 * @author Luca Dinies
 */
public class TokenEditor extends CustomField<Set<Token>> {

    private static final long serialVersionUID = 8157418441923421547L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenEditor.class);

    Grid<Token> tokenGrid;
    Set<Token> gridItems;

    ExerciseSchemeService exerciseSchemeService;

    public TokenEditor(ExerciseSchemeService exerciseSchemeService) {
        Label label = new Label("Tokens");
        add(label);
        this.exerciseSchemeService = exerciseSchemeService;

        tokenGrid = new Grid<>();
        gridItems = new HashSet<>();
        tokenGrid.setWidth("100%");
        tokenGrid.setItems(gridItems);
        tokenGrid.addColumn(Token::getName).setAutoWidth(true);
        tokenGrid.addComponentColumn(item -> createHiWiAccessCheckbox(item));
        tokenGrid.addComponentColumn(item -> createDeleteButton(item));
        tokenGrid.setSelectionMode(Grid.SelectionMode.NONE);
        tokenGrid.setHeight("15em");
        add(tokenGrid);

        FormLayout tokenFormLayout = new FormLayout();
        tokenFormLayout.setWidthFull();
        setWidthFull();
        tokenFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("5em", 1),
                new FormLayout.ResponsiveStep("5em", 2));

        TextField addedToken = new TextField();
        addedToken.setPlaceholder("Token");
        tokenFormLayout.add(addedToken);

        Button tokenButton = new Button("Token Hinzufügen", event -> {
            gridItems.add(new Token(addedToken.getValue(), false));
            tokenGrid.getDataProvider().refreshAll();
            setValue(new HashSet<>(gridItems));
            addedToken.clear();
        });

        tokenFormLayout.add(tokenButton);
        add(tokenFormLayout);
    };

    @Override
    protected Set<Token> generateModelValue() {
        Set<Token> modelValue = new HashSet<>();

        modelValue.addAll(gridItems);
        return modelValue;
    }

    @Override
    protected void setPresentationValue(Set<Token> newPresentationValue) {
        gridItems.clear();
        newPresentationValue.stream().forEach(token -> gridItems.add(token.copy()));

        tokenGrid.getDataProvider().refreshAll();
    }

    private Button createDeleteButton(Token item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            gridItems.remove(item);
            tokenGrid.getDataProvider().refreshAll();
            setValue(new HashSet<>(gridItems));
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return button;
    }

    private Checkbox createHiWiAccessCheckbox(Token item) {
        Checkbox checkbox = new Checkbox("HiWi", changeEvent -> {
            item.setAssignableByHIWI(changeEvent.getValue());
            LOGGER.debug(String.valueOf(item.getAssignableByHIWI()));
        });
        checkbox.setValue(item.getAssignableByHIWI());
        return checkbox;
    }
}
