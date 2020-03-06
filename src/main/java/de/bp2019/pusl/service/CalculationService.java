package de.bp2019.pusl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.model.StudentPerformance;
import de.bp2019.pusl.util.exceptions.JSException;

public class CalculationService {

    public StudentPerformance calculatePerformance(List<Grade> grades, Lecture lecture, String matrNumber) {

        List<Object> gradeValues = new ArrayList<>();

        for (Exercise exercise : lecture.getExercises()) {
            Optional<Grade> grade = grades.stream()
                        .filter(g -> g.getExercise().equals(exercise) && g.getMatrNumber().equals(matrNumber))
                        .findFirst();

            if (grade.isPresent()) {
                if (exercise.getScheme().getIsNumeric()) {
                    gradeValues.add(Float.valueOf(grade.get().getGrade()));
                } else {
                    gradeValues.add(grade.get().getGrade());
                }
            } else {
                gradeValues.add(exercise.getScheme().getDefaultValue());
            }
        }

        StudentPerformance result = new StudentPerformance(matrNumber, lecture);

        for(PerformanceScheme performanceScheme: lecture.getPerformanceSchemes()){
            String calculationResult = calculate(performanceScheme.getCalculationRule(), gradeValues.toArray());
            result.setPerformance(performanceScheme, calculationResult);
        }

        return result;
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