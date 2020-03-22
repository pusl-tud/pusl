package de.bp2019.pusl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Performance;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.util.exceptions.JSException;

@Service
public class CalculationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationService.class);

    @Autowired
    GradeService gradeService;

    public List<Performance> calculatePerformances(List<String> matrNumbers, Lecture lecture,
            PerformanceScheme performanceScheme) {
        List<Performance> result = new ArrayList<>();

        try{
            matrNumbers.forEach(matrNumber -> {
                result.add(calculatePerformance(matrNumber, lecture, performanceScheme));
            });
        } catch(Exception e) {
            ErrorDialog.open("Es gab einen Fehler mit der Berechnungsregel");

            matrNumbers.forEach(matrNumber -> {
                result.add(new Performance(matrNumber, performanceScheme, " "));
            });
        }

        return result;
    }

    public Performance calculatePerformance(String matrNumber, Lecture lecture, PerformanceScheme performanceScheme) {

        Grade filter = new Grade();
        filter.setMatrNumber(matrNumber);
        filter.setLecture(lecture);
        //////////////////////////////////////
        List<Grade> grades = new ArrayList<>();
        LOGGER.info(grades.toString());

        List<Object> gradeValues = new ArrayList<>();

        for (Exercise exercise : lecture.getExercises()) {
            Optional<Grade> grade = grades.stream()
                    .filter(g -> g.getExercise().equals(exercise) && g.getMatrNumber().equals(matrNumber)).findFirst();

            if (grade.isPresent()) {
                if (exercise.getScheme().getIsNumeric()) {
                    gradeValues.add(Float.valueOf(grade.get().getValue()));
                } else {
                    gradeValues.add(grade.get().getValue());
                }
            } else {
                gradeValues.add(exercise.getScheme().getDefaultValue());
            }
        }

        String calculationResult = calculate(performanceScheme.getCalculationRule(), gradeValues.toArray());

        return new Performance(matrNumber, performanceScheme, calculationResult);
    }

    public String calculate(String script, Object[] grades) {
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();

        cx.evaluateString(scope, script, "pusl", 1, null);

        Object fObj = scope.get("calculate", scope);
        String report = "";

        Object functionArgs[] = { grades };
        Function f = (Function) fObj;
        Object result = f.call(cx, scope, scope, functionArgs);
        report = Context.toString(result);

        Context.exit();

        return report;
    }

    public void checkPerformanceScheme(PerformanceScheme performanceScheme) throws JSException {
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();

        cx.evaluateString(scope, performanceScheme.getCalculationRule(), "pusl", 1, null);

        Object fObj = scope.get("calculate", scope);

        if (!(fObj instanceof Function)) {
            throw new JSException();
        }
    }
}