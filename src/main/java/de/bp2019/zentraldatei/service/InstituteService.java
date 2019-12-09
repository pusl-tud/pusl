package de.bp2019.zentraldatei.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.repository.InstituteRepository;

/**
 * Service providing relevant Institutes
 * 
 * @author Leon Chemnitz
 */
@Service
public class InstituteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstituteService.class);

    @Autowired
    InstituteRepository instituteRepository;

    public InstituteService() {
    }

    /**
     * Get all Institutes the User is authenticated to see.
     * 
     * @author Leon Chemnitz
     */
    public List<Institute> getAllInstitutes() {
        // TODO: implement authentication
        return instituteRepository.findAll();
    }

    /**
     * Get just the Ids of all the Institutes the User is authenticated to see.
     * 
     * @author Leon Chemnitz
     */
    public List<String> getAllInstituteIDs() {
        // TODO: implement authentication
        return instituteRepository.findAll().stream().map(Institute::getId).collect(Collectors.toList());
    }

    /**
     * Get a Institute based on its Id. Only returns Institutes the User is
     * authenticated to see.
     * 
     * @param Id Id to search for
     * @return found Institute with maching Id, null if none is found
     * @author Leon Chemnitz
     */
    public Institute getInstituteById(String id) {
        // TODO: implement authentication
        Optional<Institute> foundInstitute = instituteRepository.findById(id);

        if (foundInstitute.isPresent()) {
            return foundInstitute.get();
        } else {
            LOGGER.warn("Tried to get Institute which doesn't exist in Database! Institute ID was: " + id);
            return null;
        }
    }
}