package de.bp2019.zentraldatei.UI.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import de.bp2019.zentraldatei.model.Token;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Component used as a Field to add and remove Tokens in a
 * list. Used in ExerciseSchemeView.
 *
 * @author Luca Dinies
 */

public class TokenEditor extends CustomField<Set<String>> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenEditor.class);

    Grid<Token> tokenGrid;
    List<Token> gridItems;

    ExerciseSchemeService exerciseSchemeService;

    public TokenEditor(ExerciseSchemeService exerciseSchemeService) {
        Label label = new Label("Tokens");
        add(label);
        this.exerciseSchemeService = exerciseSchemeService;

        tokenGrid = new Grid<>();
        gridItems = new ArrayList<Token>();
        tokenGrid.setWidth("100%");
        tokenGrid.setItems(gridItems);
        tokenGrid.addColumn(Token::getTokenName).setAutoWidth(true);
        tokenGrid.addComponentColumn(item -> createDeleteButton(item));
        tokenGrid.setSelectionMode(Grid.SelectionMode.NONE);
        tokenGrid.setHeight("15em");
        add(tokenGrid);

        FormLayout tokenFormLayout = new FormLayout();
        tokenFormLayout.setWidthFull();
        setWidthFull();
        tokenFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("5em", 1), new FormLayout.ResponsiveStep("5em", 2));

        TextField addedToken = new TextField();
        addedToken.setPlaceholder("Token");
        tokenFormLayout.add(addedToken);

        Button tokenButton = new Button("Token Hinzufügen", event -> {
            Token token = new Token(addedToken.getValue());
            gridItems.add(token);
            tokenGrid.getDataProvider().refreshAll();
            addedToken.clear();

            setValue(gridItems.stream().map(Token::getTokenName).collect(Collectors.toSet()));
        });

        tokenFormLayout.add(tokenButton);
        add(tokenFormLayout);
    };


    @Override
    protected Set<String> generateModelValue() {
        LOGGER.info(gridItems.toString());
        return gridItems.stream().map(Token::getTokenName).collect(Collectors.toSet());
    }

    @Override
    protected void setPresentationValue(Set<String> newPresentationValue) {
        gridItems.clear();
        newPresentationValue.stream().forEach(token -> gridItems.add(new Token(token)));

        tokenGrid.getDataProvider().refreshAll();
    }

    private Button createDeleteButton(Token item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            gridItems.remove(item);
            tokenGrid.getDataProvider().refreshAll();

            /* VERY INEFFICIENT!! BETTER SOLUTION NEEDED */
            setValue(gridItems.stream().map(Token::getTokenName).collect(Collectors.toSet()));
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return button;
    }

}
