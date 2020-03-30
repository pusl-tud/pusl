package de.bp2019.pusl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.data.provider.Query;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.GradeFilter;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Performance;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;

/**
 * Service providing functionality to execute JavaScript functions defined in
 * {@link PerformanceScheme}s
 * 
 * @author Leon Chemnitz
 */
@Service
@Scope("session")
public class CalculationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationService.class);

    @Autowired
    GradeService gradeService;

    /**
     * Calulates the performances of students with matrNumbers given as argument.
     * Looks up all relevant {@link Grade}s from Database.
     * 
     * @param matrNumbers list of matrNumbers to evaluate
     * @param lecture lecture to calculate
     * @param performanceScheme performanceScheme to calculate
     * @return list of calculated performances
     * @author Leon Chemnitz
     */
    public List<Performance> calculatePerformances(List<String> matrNumbers, Lecture lecture,
            PerformanceScheme performanceScheme) {
        List<Performance> result = new ArrayList<>();

        try {
            matrNumbers.forEach(matrNumber -> result.add(calculatePerformance(matrNumber, lecture, performanceScheme)));
        } catch (Exception e) {
            LOGGER.error(e.toString());
            ErrorDialog.open("Es gab einen Fehler mit der Berechnungsregel");

            matrNumbers.forEach(matrNumber -> result.add(new Performance(matrNumber, performanceScheme, " ")));
        }

        return result;
    }

    /**
     * Calculate a single perfomance. Looks up all relevant {@link Grade}s from
     * Database.
     * 
     * @param matrNumber matrNumber to evaluate
     * @param lecture lecture to calculate
     * @param performanceScheme performanceScheme to calculate
     * @return resulting performance
     * @author Leon Chemnitz
     */
    public Performance calculatePerformance(String matrNumber, Lecture lecture, PerformanceScheme performanceScheme) {

        GradeFilter filter = new GradeFilter();
        filter.setMatrNumber(matrNumber);
        filter.setLecture(lecture);

        List<Grade> grades = gradeService.fetch(new Query<>(), filter).collect(Collectors.toList());
        LOGGER.debug("fetched grades for matr Number " + matrNumber + " : " + grades.toString());

        List<Object> gradeValues = new ArrayList<>();

        for (Exercise exercise : lecture.getExercises()) {
            Optional<Grade> grade = grades.stream()
                    .filter(g -> g.getExercise().equals(exercise) && g.getMatrNumber().equals(matrNumber)).findFirst();

            ExerciseScheme exerciseScheme = exercise.getScheme();
            if (grade.isPresent()) {
                if (exerciseScheme.isNumeric()) {
                    gradeValues.add(Float.valueOf(grade.get().getValue()));
                } else {
                    gradeValues.add(grade.get().getValue());
                }
            } else {
                if (exerciseScheme.isNumeric()) {
                    gradeValues.add(exerciseScheme.getDefaultValueNumeric());
                } else {
                    gradeValues.add(exerciseScheme.getDefaultValueToken().getName());
                }
            }
        }

        String calculationResult = calculate(performanceScheme.getCalculationRule(), gradeValues.toArray());

        return new Performance(matrNumber, performanceScheme, calculationResult);
    }

    /**
     * 
     * @param script JS calculation rule
     * @param grades exercise grades
     * @return calculation result
     * @author Leon Chemnitz
     */
    private String calculate(String script, Object[] grades) {
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();

        cx.evaluateString(scope, script, "pusl", 1, null);

        Object fObj = scope.get("calculate", scope);
        String report = "";

        Object[] functionArgs = { grades };
        Function f = (Function) fObj;
        Object result = f.call(cx, scope, scope, functionArgs);
        report = Context.toString(result);

        Context.exit();

        return report;
    }

    // public void checkPerformanceScheme(PerformanceScheme performanceScheme)
    // throws JSException {
    // Context cx = Context.enter();
    // Scriptable scope = cx.initStandardObjects();

    // cx.evaluateString(scope, performanceScheme.getCalculationRule(), "pusl", 1,
    // null);

    // Object fObj = scope.get("calculate", scope);

    // if (!(fObj instanceof Function)) {
    // throw new JSException();
    // }
    // }
}