package de.bp2019.zentraldatei.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.ExerciseScheme;

/**
 * Service providing relevant ExerciseSchemes
 * @author Leon Chemnitz
 */
@Service
public class ExerciseSchemeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseSchemeService.class);

    private List<ExerciseScheme> allExerciseSchemes;

    public ExerciseSchemeService() {
        LOGGER.debug("Started creation of ExerciseSchemeService");

        /* Platzhalter Code da noch keine echten Repositories existieren */
        allExerciseSchemes = new ArrayList<ExerciseScheme>();

        allExerciseSchemes.add(new ExerciseScheme(null, "Testat", false, null, null));
        allExerciseSchemes.add(new ExerciseScheme(null, "Übung", false, null, null));
        allExerciseSchemes.add(new ExerciseScheme(null, "Klausur", false, null, null));

        LOGGER.debug("Finished creation of ExerciseSchemeService");
    }

    public List<ExerciseScheme> getAllExerciseSchemes() {
        return allExerciseSchemes;
    }

}