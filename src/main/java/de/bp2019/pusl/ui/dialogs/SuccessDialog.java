package de.bp2019.pusl.ui.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;

/**
 * Simple Dialog displaying a Success Message
 * 
 * @author Leon Chemnitz
 */
public final class SuccessDialog {
    public static void open(String msg) {
        Dialog dialog = new Dialog();
        dialog.setId("success-dialog");
        Label label = new Label(msg);

        ShortcutRegistration registration = UI.getCurrent().addShortcutListener(() -> dialog.close(), Key.ENTER);
        dialog.addOpenedChangeListener(event -> {
            if(event.isOpened() == false) {
                registration.remove();
            }
        });
        
        dialog.add(label);
        dialog.open();
    }
}