package de.bp2019.pusl.ui.components;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.service.dataproviders.GradeFilter;
import de.bp2019.pusl.util.Service;

/**
 * @author Leon Chemnitz
 */
public class GradeComposer extends CustomField<GradeFilter> {

    private static final long serialVersionUID = -295095973291227730L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeComposer.class);

    private LectureService lectureService;

    private ComboBox<Lecture> lecture;
    private ComboBox<Exercise> exercise;
    private IntegerField matrNumber;
    private NumberField numeric;
    private ComboBox<Token> token;

    private GradeFilter value;

    public GradeComposer() {
        lectureService = Service.get(LectureService.class);

        value = new GradeFilter();

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2),
                new ResponsiveStep("5em", 3), new ResponsiveStep("5em", 4));
        layout.setWidthFull();

        matrNumber = new IntegerField();
        matrNumber.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        matrNumber.setLabel("Matrikelnummer");
        matrNumber.setPlaceholder("Matrikelnummer");
        matrNumber.setValueChangeMode(ValueChangeMode.EAGER);
        layout.add(matrNumber, 1);

        lecture = new ComboBox<>();
        lecture.getElement().setAttribute("theme", "small");
        lecture.setItemLabelGenerator(Lecture::getName);
        lecture.setDataProvider(lectureService);
        lecture.setLabel("Veranstaltung");
        lecture.setClearButtonVisible(true);
        layout.add(lecture, 1);

        exercise = new ComboBox<>();
        exercise.getElement().setAttribute("theme", "small");
        exercise.setItemLabelGenerator(Exercise::getName);
        exercise.setLabel("Übung");
        exercise.setClearButtonVisible(true);
        exercise.setEnabled(false);
        layout.add(exercise, 1);

        numeric = new NumberField();
        numeric.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        numeric.setLabel("Note");
        numeric.setEnabled(false);
        layout.add(numeric, 1);

        token = new ComboBox<>();
        token.getElement().setAttribute("theme", "small");
        token.setItemLabelGenerator(Token::getName);
        token.setLabel("Token");
        token.setVisible(false);
        token.setClearButtonVisible(true);
        token.setEnabled(false);
        layout.add(token, 1);

        add(layout);

        matrNumber.addValueChangeListener(event -> {
            value.setMatrNumber(event.getValue().toString());
            setValue(new GradeFilter(value));
        });

        lecture.addValueChangeListener(event -> {
            Lecture lectureValue = event.getValue();

            value.setLecture(lectureValue);  

            if(exercise.getValue() == null){
                setValue(new GradeFilter(value));
            }   

            if (lectureValue == null) {
                LOGGER.debug("lecture empty");
                exercise.setItems(Stream.empty());
                exercise.setEnabled(false);
            } else {
                LOGGER.debug("lecture not empty");
                exercise.setItems(lectureValue.getExercises());
                exercise.setEnabled(true);
            }       
        });

        exercise.addValueChangeListener(event -> {
            Exercise exerciseValue = event.getValue();

            value.setExercise(exerciseValue);

            // if(numeric.getValue() == null){
            //     setValue(new GradeFilter(value));
            // }

            if (exerciseValue == null) {
                LOGGER.debug("exercise empty");
                token.setVisible(false);
                token.setEnabled(false);

                numeric.setVisible(true);
                numeric.setValue(null);
                numeric.setEnabled(false);

            } else {
                LOGGER.debug("exercise not empty");
                ExerciseScheme exerciseSchemeValue = exerciseValue.getScheme();

                if (exerciseSchemeValue.getIsNumeric()) {
                    LOGGER.debug("exercise numeric");
                    token.setVisible(false);
                    token.setEnabled(false);

                    numeric.setEnabled(true);
                    numeric.setValue(null);
                    numeric.setVisible(true);
                } else {
                    LOGGER.debug("exercise not numeric");
                    numeric.setEnabled(false);
                    numeric.setVisible(false);
                    numeric.setValue(null);

                    token.setVisible(true);
                    token.setEnabled(true);
                    token.setItems(exerciseSchemeValue.getTokens());
                    token.setValue(null);
                }
            }
        });

        numeric.addValueChangeListener(event -> {
            LOGGER.debug("setting value numeric");
            Double numericValue = event.getValue();
            String gradeValue;

            if (numericValue == null) {
                LOGGER.debug("numeric empty");
                gradeValue = "";
            } else {
                LOGGER.debug("numeric not empty");
                gradeValue = numericValue.toString();
            }

            value.setGrade(gradeValue);
            setValue(new GradeFilter(value));
        });

        token.addValueChangeListener(event -> {
            LOGGER.debug("setting value token");
            Token tokenValue = event.getValue();

            String gradeValue;

            if (tokenValue == null) {
                LOGGER.debug("token empty");
                gradeValue = "";
            } else {
                LOGGER.debug("token not empty");
                gradeValue = tokenValue.getName();
            }

            value.setGrade(gradeValue);
            setValue(new GradeFilter(value));
        });
    }

    @Override
    protected GradeFilter generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(GradeFilter newPresentationValue) {
        this.value = newPresentationValue;

        if(value.getMatrNumber() != null){
            matrNumber.setValue(Integer.valueOf(value.getMatrNumber()));
        }else {
            matrNumber.setValue(null);
        }

        Lecture lectureValue = value.getLecture();
        Exercise exerciseValue = value.getExercise();

        lecture.setValue(lectureValue);
        
        if(lectureValue != null){
            exercise.setItems(lectureValue.getExercises());
        }

        exercise.setValue(exerciseValue);

        if (exerciseValue != null) {

            String gradeValue = value.getGrade();
            ExerciseScheme exerciseScheme = exerciseValue.getScheme();
            LOGGER.debug("Grade value: " + gradeValue);

            if (exerciseScheme.getIsNumeric()) {
                if(gradeValue != null && NumberUtils.isParsable(gradeValue)) {
                    numeric.setValue(Double.valueOf(gradeValue));
                }else{
                    numeric.setValue(null);
                }
            } else {
                token.setItems(exerciseScheme.getTokens());

                Set<Token> tokens = value.getExercise().getScheme().getTokens();
                LOGGER.debug(tokens.toString());

                Optional<Token> tokenValue = tokens.stream().filter(t -> t.getName().equals(gradeValue)).findFirst();

                if(tokenValue.isPresent()){
                    LOGGER.debug("token found");
                    token.setValue(tokenValue.get());
                }else{
                    LOGGER.debug("token not found");
                    token.setValue(null);
                }
            }
        }

    }

}