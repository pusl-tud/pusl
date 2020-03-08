package de.bp2019.pusl.ui.dialogs;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;

/**
 * Simple Dialog displaying a Success Message
 * 
 * @author Leon Chemnitz
 */
public final class SuccessDialog {
    public static void open(String msg){
        Dialog dialog = new Dialog();
        Label label = new Label(msg);
        dialog.add(label);
        dialog.open();
    }
}