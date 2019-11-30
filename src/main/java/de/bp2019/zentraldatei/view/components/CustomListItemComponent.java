package de.bp2019.zentraldatei.view.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that is used as an Item in CustomListComponent
 * 
 * @param <T> Type of the List Item
 * @author Leon Chemnitz
 */
public class CustomListItemComponent<T extends ICustomListItem> extends HorizontalLayout {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomListItemComponent.class);

    T item;

    private Button itemButton;
    private Button duplicateButton;
    private Button deleteButton;

    public CustomListItemComponent(T item) {
        this.item = item;

        itemButton = new Button(item.getName());
        itemButton.addThemeVariants(ButtonVariant.LUMO_SMALL,
        ButtonVariant.LUMO_TERTIARY);
        add(itemButton);

        duplicateButton = new Button("Duplizieren", new Icon(VaadinIcon.ANGLE_DOUBLE_DOWN));
        duplicateButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        add(duplicateButton);

        deleteButton = new Button(new Icon(VaadinIcon.CLOSE));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL,
        ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        add(deleteButton);

    }

}