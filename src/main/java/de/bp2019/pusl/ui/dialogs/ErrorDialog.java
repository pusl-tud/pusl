package de.bp2019.pusl.ui.dialogs;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;

/**
 * Simple Dialog displaying an Error Message
 * 
 * @author Leon Chemnitz
 */
public final class ErrorDialog {
    public static void open(String msg){
        Dialog dialog = new Dialog();
        Label label = new Label(msg);
        label.getStyle().set("font-weight", "600");
        label.getStyle().set("color", "var(--lumo-error-color)");
        dialog.add(label);
        dialog.open();
    }
}