package de.bp2019.pusl.ui.dialogs;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.TUCanEntity;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.service.AuthenticationService;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.service.dataproviders.FilteringGradeDataProvider;
import de.bp2019.pusl.ui.views.WorkView;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.Utils;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * Dialog to import {@link Grade}s from an ExcelSheet. Used in {@link WorkView}
 * 
 * @author Leon Chemnitz
 */
public final class ImportGradesDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportGradesDialog.class);

    public static void open(List<TUCanEntity> entities, FilteringGradeDataProvider filteringGradeDataProvider) {
        LOGGER.debug("opening ImportGradesDialog");

        GradeService gradeService = Service.get(GradeService.class);
        LectureService lectureService = Service.get(LectureService.class);
        AuthenticationService authService = Service.get(AuthenticationService.class);

        Dialog dialog = new Dialog();
        dialog.setWidth("700px");

        VerticalLayout layout = new VerticalLayout();

        Label title = new Label("Einzelleistungen importieren");
        title.getStyle().set("font-size", "1.5em");
        layout.add(title);

        Label warning = new Label("");
        warning.getStyle().set("color", "red");
        warning.getStyle().set("white-space", "pre-line");
        layout.add(warning);

        DatePicker handInDatePicker = new DatePicker();
        handInDatePicker.setLabel("Abgabe-Datum");
        handInDatePicker.setValue(LocalDate.now());
        handInDatePicker.setWidth("300px");
        layout.add(handInDatePicker);

        ComboBox<Exercise> exerciseField = new ComboBox<>();
        ListDataProvider<TUCanEntity> dataProvider = new ListDataProvider<>(entities);
        Grid<TUCanEntity> grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.setHeight("20em");
        grid.setDataProvider(dataProvider);

        grid.addColumn(TUCanEntity::getMatrNumber).setHeader("Matrikel-Nummer").setAutoWidth(true)
                .setClassNameGenerator(entity -> {
                    if (!Utils.isMatrNumber(entity.getMatrNumber())) {
                        return "background-yellow";
                    } else
                        return null;
                });

        grid.addColumn(TUCanEntity::getGrade).setHeader("Bewertung").setClassNameGenerator(entity -> {
            if (!gradeMatchesExercise(entity.getGrade(), exerciseField.getValue())) {
                return "background-red";
            } else
                return null;
        });

        layout.add(grid);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new ResponsiveStep("5em", 3));
        form.setWidthFull();

        ComboBox<Lecture> lectureField = new ComboBox<>();
        lectureField.getElement().setAttribute("theme", "small");
        lectureField.setItemLabelGenerator(Lecture::getName);
        lectureField.setDataProvider(lectureService);
        lectureField.setLabel("Veranstaltung");
        lectureField.setClearButtonVisible(true);
        form.add(lectureField, 1);

        exerciseField.getElement().setAttribute("theme", "small");
        exerciseField.setItemLabelGenerator(Exercise::getName);
        exerciseField.setLabel("Leistung");
        exerciseField.setClearButtonVisible(true);
        exerciseField.setEnabled(false);
        form.add(exerciseField, 1);

        Button importButton = new Button("importieren");
        importButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        importButton.setEnabled(false);
        form.add(importButton);

        lectureField.addValueChangeListener(event -> {
            LOGGER.debug("setting lecture");

            Lecture lectureValue = event.getValue();

            if (lectureValue == null) {
                LOGGER.debug("lecture empty");
                exerciseField.setItems(Stream.empty());
                exerciseField.setEnabled(false);
            } else {
                LOGGER.debug("lecture not empty");
                exerciseField.setItems(lectureValue.getExercises());
                exerciseField.setEnabled(true);
            }
        });

        exerciseField.addValueChangeListener(event -> {
            LOGGER.debug("setting Exercise");

            Exercise exerciseValue = event.getValue();

            if (exerciseValue == null) {
                LOGGER.debug("exercise empty");
                importButton.setEnabled(false);
            } else {
                LOGGER.debug("exercise not empty");
                importButton.setEnabled(true);
            }

            int items = entities.size();
            long faultyItems = entities.stream()
                    .filter(entity -> !gradeMatchesExercise(entity.getGrade(), exerciseField.getValue())).count();

            setWarningLabel(warning, items, faultyItems);

            dataProvider.refreshAll();
        });

        importButton.addClickListener(event -> {
            List<TUCanEntity> correctGradeEntities = entities.stream()
                    .filter(entity -> gradeMatchesExercise(entity.getGrade(), exerciseField.getValue()))
                    .collect(Collectors.toList());
            List<TUCanEntity> correctMatrEntities = correctGradeEntities.stream()
                    .filter(entity -> Utils.isMatrNumber(entity.getMatrNumber())).collect(Collectors.toList());

            Lecture lecture = lectureField.getValue();
            Exercise exercise = exerciseField.getValue();
            LocalDate handIn = handInDatePicker.getValue();

            if (correctGradeEntities.size() == 0) {
                SuccessDialog.open("Nichts zu importieren");
                dialog.close();
            } else if (correctGradeEntities.size() == correctMatrEntities.size()) {
                List<Grade> grades = correctGradeEntities.stream().map(entity -> new Grade(lecture, exercise,
                        entity.getMatrNumber(), entity.getGrade(), handIn, authService.currentUserId()))
                        .collect(Collectors.toList());
                try {
                    gradeService.save(grades);
                    SuccessDialog.open("Einzelleistungen wurden erfolgreich importiert");
                    filteringGradeDataProvider.refreshAll();
                } catch (UnauthorizedException e) {
                    LOGGER.info("current User unauthorized to save Grades");
                    ErrorDialog.open("nicht authoriziert um Einzelleistungen zu speichern");
                } finally {
                    dialog.close();
                }

            } else {
                long numFaultyMatrEntities = correctGradeEntities.size() - correctMatrEntities.size();

                LOGGER.debug("Entries with correct Exercisescheme: " + correctGradeEntities.size());
                LOGGER.debug("Entries with correct ES and correct matr: " + correctMatrEntities.size());
                LOGGER.debug("Entries with correct ES and faulty matr: " + numFaultyMatrEntities);

                Dialog acceptDialog = new Dialog();
                acceptDialog.setWidth("700px");

                VerticalLayout acceptDialogLayout = new VerticalLayout();

                Label message = new Label(numFaultyMatrEntities + " Einträge enthalten fehlerhafte Matrikelnummern.");
                message.getStyle().set("font-weight", "600");
                message.getStyle().set("padding-bottom", "1em");

                acceptDialogLayout.add(message);

                FormLayout buttonLayout = new FormLayout();
                buttonLayout.setResponsiveSteps(new ResponsiveStep("5em", 3));
                buttonLayout.setWidthFull();

                Button abortButton = new Button("abbrechen");
                buttonLayout.add(abortButton, 1);

                Button importAllButton = new Button("trotzdem importieren");
                buttonLayout.add(importAllButton, 1);

                Button importCorrectButton = new Button("korrekte importieren");
                buttonLayout.add(importCorrectButton, 1);
                importCorrectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                acceptDialogLayout.add(buttonLayout);
                acceptDialog.add(acceptDialogLayout);

                abortButton.addClickListener(clickEvent -> acceptDialog.close());

                importAllButton.addClickListener(clickEvent -> {
                    List<Grade> grades = correctGradeEntities
                            .stream().map(entity -> new Grade(lecture, exercise, entity.getMatrNumber(),
                                    entity.getGrade(), handIn, authService.currentUserId()))
                            .collect(Collectors.toList());
                    try {
                        gradeService.save(grades);
                        SuccessDialog.open("Einzelleistungen wurden erfolgreich importiert");
                        filteringGradeDataProvider.refreshAll();
                    } catch (UnauthorizedException e) {
                        LOGGER.info("current User unauthorized to save Grades");
                        ErrorDialog.open("nicht authoriziert um Einzelleistungen zu speichern");
                    } finally {
                        acceptDialog.close();
                        dialog.close();
                    }
                });

                importCorrectButton.addClickListener(clickEvent -> {
                    List<Grade> grades = correctMatrEntities
                            .stream().map(entity -> new Grade(lecture, exercise, entity.getMatrNumber(),
                                    entity.getGrade(), handIn, authService.currentUserId()))
                            .collect(Collectors.toList());
                    try {
                        gradeService.save(grades);
                        SuccessDialog.open("Einzelleistungen wurden erfolgreich importiert");
                        filteringGradeDataProvider.refreshAll();
                    } catch (UnauthorizedException e) {
                        LOGGER.info("current User unauthorized to save Grades");
                        ErrorDialog.open("nicht authoriziert um Einzelleistungen zu speichern");
                    } finally {
                        acceptDialog.close();
                        dialog.close();
                    }
                });

                acceptDialog.open();
            }

        });

        layout.add(form);
        dialog.add(layout);
        dialog.open();
    }

    private static void setWarningLabel(Label label, int items, long faultyItems) {
        if (faultyItems > 0) {
            label.setVisible(true);
            label.setText("Einige Einträge sind nicht mit dem ausgewählten Leistungsschema kompatibel.\nVon " + items
                    + " Einträgen können " + (items - faultyItems) + " importiert werden.");
        } else {
            label.setVisible(false);
        }
    }

    private static boolean gradeMatchesExercise(String grade, Exercise exercise) {
        if (exercise == null) {
            return true;
        }

        ExerciseScheme scheme = exercise.getScheme();

        if (scheme.isNumeric()) {
            return NumberUtils.isCreatable(grade);
        } else {
            return scheme.getTokens().stream().map(Token::getName).collect(Collectors.toList()).contains(grade);
        }
    }
}
