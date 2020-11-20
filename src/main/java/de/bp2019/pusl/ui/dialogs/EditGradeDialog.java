package de.bp2019.pusl.ui.dialogs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.GradeFilter;
import de.bp2019.pusl.service.AuthenticationService;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.ui.components.GradeComposer;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;
import de.bp2019.pusl.ui.views.WorkView;

/**
 * Dialog to edit a {@link Grade}. Used in {@link WorkView}
 * 
 * @author Leon Chemnitz
 */
public final class EditGradeDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditGradeDialog.class);

    public static void open(Grade grade, Runnable callback) {
        LOGGER.debug("opening EditGradeDialog");
        LOGGER.debug(grade.toString());

        AuthenticationService authenticationService = Service.get(AuthenticationService.class);

        GradeService gradeService = Service.get(GradeService.class);

        Dialog dialog = new Dialog();

        VerticalLayout info = new VerticalLayout();

        Label title = new Label("Einzelleistung bearbeiten");
        title.getStyle().set("font-size", "1.5em");
        info.add(title);

        Label lastModified = new Label("Zuletzt geändert am: "
                + grade.getLastModified().format(DateTimeFormatter.ofPattern("dd. MM. uuuu | HH:mm")));
        lastModified.getStyle().set("margin-top", "0");
        info.add(lastModified);
        
        String userName = Grade.getNameOfGradedBy(grade);
        Label gradedBy = new Label("von: " + userName);
        gradedBy.getStyle().set("margin-top", "0");
        info.add(gradedBy);
        info.getStyle().set("padding", "0");

        dialog.add(info);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2),
                new ResponsiveStep("5em", 3), new ResponsiveStep("5em", 4));
        GradeComposer gradeComposer = new GradeComposer();
        gradeComposer.setId("dialog-gc");
        GradeFilter value = new GradeFilter(grade);
        gradeComposer.setValue(value);
        form.add(gradeComposer, 4);

        Button delete = new Button("löschen");
        delete.addThemeVariants(ButtonVariant.LUMO_SMALL);
        form.add(delete, 1);

        form.add(new Label(""), 1);

        DatePicker handIn = new DatePicker();
        handIn.getElement().setAttribute("theme", "small");
        handIn.setLabel("Abgabe-Datum");
        handIn.setValue(grade.getHandIn());
        form.add(handIn, 1);

        Button save = new Button("speichern");
		save.addClickShortcut(Key.ENTER);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        form.add(save, 1);

        dialog.add(form);
        dialog.open();

        delete.addClickListener(event -> {
            ConfirmDeletionDialog.open(grade.getMatrNumber(), () -> {

                try {
                    Grade toDelete = gradeService.getById(grade.getId());

                    gradeService.delete(toDelete);
                    SuccessDialog.open("Einzelleistung erfolgreich gelöscht");
                    callback.run();
                } catch (DataNotFoundException e) {
                    LOGGER.error("Grade not found in Database");
                    ErrorDialog.open("zu bearbeitende Einzelleistung wurde nicht in Datenbank gefunden");
                } catch (UnauthorizedException e) {
                    LOGGER.error("unauthorized to save Grade");
                    ErrorDialog.open("Nicht authorisiert um Einzelleistung zu bearbeiten!");
                } finally {
                    dialog.close();
                }
            });
        });

        save.addClickListener(event -> {
            GradeFilter composerValue = gradeComposer.getValue();

            Grade modified;
            try {
                modified = gradeService.getById(grade.getId());
            } catch (DataNotFoundException e) {
                LOGGER.error("Grade not found in Database");
                ErrorDialog.open("zu bearbeitende Einzelleistung wurde nicht in Datenbank gefunden");
                return;
            } catch (UnauthorizedException e) {
                LOGGER.error("unauthorized to save Grade");
                ErrorDialog.open("Nicht authorisiert um Einzelleistung zu bearbeiten!");
                return;
            }

            modified.setMatrNumber(composerValue.getMatrNumber());
            modified.setLecture(composerValue.getLecture());
            modified.setExercise(composerValue.getExercise());
            modified.setValue(composerValue.getGrade());

            modified.setHandIn(handIn.getValue());

            modified.setLastModified(LocalDateTime.now());
            modified.setGradedBy(authenticationService.currentUser());

            if (!GradeService.gradeIsValid(modified)) {
                return;
            }

            try {
                gradeService.save(modified);
                SuccessDialog.open("Note erfolgreich gespeichert");
                callback.run();
            } catch (UnauthorizedException e) {
                LOGGER.error("unauthorized to save Grade");
                ErrorDialog.open("Nicht authorisiert um Einzelleistung zu bearbeiten!");
            } finally {
                dialog.close();
            }
        });
    }
}