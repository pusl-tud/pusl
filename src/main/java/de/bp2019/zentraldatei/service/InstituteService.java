package de.bp2019.zentraldatei.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.Institute;

/**
 * Service providing relevant Institutes
 * @author Leon Chemnitz
 */
@Service
public class InstituteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstituteService.class);

    List<Institute> allInstitutes;

    public InstituteService() {
        LOGGER.debug("Started  creation of InstituteService");

        /* Platzhalter Code da noch keine echten Repositories existieren */
        allInstitutes = new ArrayList<Institute>();
        
        allInstitutes.add(new Institute("Bahntechnik"));
        allInstitutes.add(new Institute("Straßenwesen"));
        allInstitutes.add(new Institute("Computergrafik"));

        LOGGER.debug("Finished creation of InstituteService");
    }

    public List<Institute> getAllInstitutes() {
        return allInstitutes;
    }

}