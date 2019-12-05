package de.bp2019.zentraldatei.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.ExcerciseScheme;

/**
 * Service providing relevant ExcerciseSchemes
 * @author Leon Chemnitz
 */
@Service
public class ExcerciseSchemeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcerciseSchemeService.class);

    private List<ExcerciseScheme> allExcerciseSchemes;

    public ExcerciseSchemeService() {
        LOGGER.debug("Started creation of ExcerciseSchemeService");

        /* Platzhalter Code da noch keine echten Repositories existieren */
        allExcerciseSchemes = new ArrayList<ExcerciseScheme>();

        allExcerciseSchemes.add(new ExcerciseScheme(null, "Testat", false, null, null));
        allExcerciseSchemes.add(new ExcerciseScheme(null, "Übung", false, null, null));
        allExcerciseSchemes.add(new ExcerciseScheme(null, "Klausur", false, null, null));

        LOGGER.debug("Finished creation of ExcerciseSchemeService");
    }

    public List<ExcerciseScheme> getAllExcerciseSchemes() {
        return allExcerciseSchemes;
    }

}