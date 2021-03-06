package de.bp2019.pusl.ui.components;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.GradeFilter;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.service.AuthenticationService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.util.Service;

/**
 * Component to configure a {@link Grade} in a dynamic way
 * 
 * @author Leon Chemnitz
 */
public class GradeComposer extends CustomField<GradeFilter> {

    private static final long serialVersionUID = -295095973291227730L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeComposer.class);

    private LectureService lectureService;
    private AuthenticationService authenticationService;

    private ComboBox<Lecture> lecture;
    private ComboBox<Exercise> exercise;
    private IntegerField matrNumber;
    private NumberField numeric;
    private ComboBox<Token> token;

    @Override
    public void setId(String id) {
        super.setId(id);

        lecture.setId(id + "-lecture");
        exercise.setId(id + "-exercise");
        matrNumber.setId(id + "-matrNumber");
        numeric.setId(id + "-numeric");
        token.setId(id + "-token");
    }

    public GradeComposer() {
        lectureService = Service.get(LectureService.class);
        authenticationService = Service.get(AuthenticationService.class);

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new ResponsiveStep("5em", 6));
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
        layout.add(lecture, 2);

        exercise = new ComboBox<>();
        exercise.getElement().setAttribute("theme", "small");
        exercise.setItemLabelGenerator(Exercise::getName);
        exercise.setLabel("Leistung");
        exercise.setClearButtonVisible(true);
        exercise.setEnabled(false);
        layout.add(exercise, 2);

        numeric = new NumberField();
        numeric.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        numeric.setLabel("Bewertung");
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
            LOGGER.debug("setting matrNumber");

            GradeFilter value = generateModelValue();
            Integer matrNumberValue = event.getValue();
            if (matrNumberValue != null) {
                value.setMatrNumber(matrNumberValue.toString());
            } else {
                value.setMatrNumber(null);
            }
            setValue(value);
        });

        lecture.addValueChangeListener(event -> {
            LOGGER.debug("setting lecture");

            Lecture lectureValue = event.getValue();

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
            LOGGER.debug("setting exercise");

            Exercise exerciseValue = event.getValue();

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

                if (exerciseSchemeValue.isNumeric()) {
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
            setModelValue(generateModelValue(), true);
        });

        token.addValueChangeListener(event -> {
            LOGGER.debug("setting value token");
            setModelValue(generateModelValue(), true);
        });
    }

    @Override
    protected GradeFilter generateModelValue() {
        GradeFilter newModelValue = new GradeFilter();

        Integer matrNumberValue = matrNumber.getValue();
        if (matrNumberValue != null) {
            newModelValue.setMatrNumber(matrNumberValue.toString());
        }

        Lecture lectureValue = lecture.getValue();
        newModelValue.setLecture(lectureValue);
        if (lectureValue != null) {
            Exercise exerciseValue = exercise.getValue();
            newModelValue.setExercise(exerciseValue);

            if (exerciseValue != null) {
                if (exerciseValue.getScheme().isNumeric()) {
                    Double numericValue = numeric.getValue();
                    if (numericValue != null) {
                        newModelValue.setGrade(numericValue.toString());
                    }
                } else {
                    Token tokenValue = token.getValue();
                    if (tokenValue != null) {
                        newModelValue.setGrade(tokenValue.getName());
                    }
                }
            }
        }

        return newModelValue;
    }

    @Override
    protected void setPresentationValue(GradeFilter value) {

        if (value.getMatrNumber() != null) {
            matrNumber.setValue(Integer.valueOf(value.getMatrNumber()));
        } else {
            matrNumber.setValue(null);
        }

        Lecture lectureValue = value.getLecture();
        Exercise exerciseValue = value.getExercise();

        lecture.setValue(lectureValue);

        if (lectureValue != null) {
            if (authenticationService.currentUserType() == UserType.HIWI) {
                List<Exercise> exercises = lectureValue.getExercises().stream().filter(e -> e.isAssignableByHIWI())
                        .collect(Collectors.toList());
                exercise.setItems(exercises);
            } else {
                exercise.setItems(lectureValue.getExercises());
            }
        }

        exercise.setValue(exerciseValue);

        if (exerciseValue != null) {

            String gradeValue = value.getGrade();
            ExerciseScheme exerciseScheme = exerciseValue.getScheme();
            LOGGER.debug("Grade value: " + gradeValue);

            if (exerciseScheme.isNumeric()) {
                if (gradeValue != null && NumberUtils.isParsable(gradeValue)) {
                    numeric.setValue(Double.valueOf(gradeValue));
                } else {
                    numeric.setValue(null);
                }
            } else {

                if (authenticationService.currentUserType() == UserType.HIWI) {
                    List<Token> tokens = exerciseScheme.getTokens().stream().filter(t -> t.getAssignableByHIWI())
                            .collect(Collectors.toList());
                    token.setItems(tokens);
                } else {
                    token.setItems(exerciseScheme.getTokens());
                }

                Set<Token> tokens = value.getExercise().getScheme().getTokens();
                LOGGER.debug(tokens.toString());

                Optional<Token> tokenValue = tokens.stream().filter(t -> t.getName().equals(gradeValue)).findFirst();

                if (tokenValue.isPresent()) {
                    LOGGER.debug("token found");
                    token.setValue(tokenValue.get());
                } else {
                    LOGGER.debug("token not found");
                    token.setValue(null);
                }
            }
        }

    }

}