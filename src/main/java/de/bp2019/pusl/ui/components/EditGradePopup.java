package de.bp2019.pusl.ui.components;

import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;

import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.service.dataproviders.GradeFilter;

/**
 * @author Leon Chemnitz
 */
public final class EditGradePopup {

    public static void open(Grade grade){
        Dialog dialog = new Dialog();
        Label label = new Label("zuletzt verändert: " + grade.getLastModified().format(DateTimeFormatter.ofPattern("dd. MM. uuuu | hh:mm")));
        dialog.add(label);

        FormLayout form = new FormLayout();        
        form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
        GradeComposer gradeComposer = new GradeComposer();
        GradeFilter value = new GradeFilter(grade);
        gradeComposer.setValue(value);
        form.add(gradeComposer, 2);

        dialog.add(form);
        dialog.open();
    }
}