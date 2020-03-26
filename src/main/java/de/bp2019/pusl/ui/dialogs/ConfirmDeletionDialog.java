package de.bp2019.pusl.ui.dialogs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Dialog displaying an Error Message
 * 
 * @author Leon Chemnitz
 */
public final class ConfirmDeletionDialog {    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmDeletionDialog.class);
    public static final String ID = "confirm-deletion-dialog";

    public static void open(String name, Runnable callback) {
        Dialog dialog = new Dialog();
        dialog.setId(ID);

        VerticalLayout layout = new VerticalLayout();
        layout.add(new Text("Möchten sie " + name + " wirklich Löschen?"));
        TextField textField = new TextField();
        textField.setId("confirm-deletion-text-field");
        layout.add(textField);
        Button confirmButton = new Button("Löschen", event -> {
            if (textField.getValue().equals(name)) {
                try {
                    callback.run();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                } finally {                    
                    dialog.close();
                }
            } else {
                textField.setErrorMessage("stimmt nicht mit " + name + " überein");
                textField.setInvalid(true);
            }
        });
        layout.add(confirmButton);

        dialog.add(layout);
        dialog.open();
    }
}