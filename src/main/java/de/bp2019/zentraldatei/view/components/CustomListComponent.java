package de.bp2019.zentraldatei.view.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component to create and edit a custom list of items
 * 
 * @param <T> ItemType to be listed
 * @author Leon Chemnitz
 */
public class CustomListComponent<T extends ICustomListItem> extends Composite<Div> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomListComponent.class);

    private List<T> items = new ArrayList<T>();

    private Label label;
    private Button addItemButton;
    private VerticalLayout itemsLayout;

    public CustomListComponent(DefaultFactory<T> factory) {
        label = new Label();
        getContent().add(label);

        addItemButton = new Button(new Icon(VaadinIcon.PLUS_SQUARE_O));
        addItemButton.addThemeVariants(ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_TERTIARY);

        itemsLayout = new VerticalLayout();

        getContent().add(itemsLayout);
        getContent().add(addItemButton);

        addItemButton.addClickListener(event -> {
            itemsLayout.add(new CustomListItemComponent<T>(factory.createDefaultInstance()));
        });
    }

    public void setButtonText(String buttonText) {
        this.addItemButton.setText(buttonText);
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public String getLabel() {
        return label.getText();
    }

    public void setLabel(String labelText) {
        this.label.setText(labelText);
    }

}