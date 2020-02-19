package de.bp2019.pusl.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.repository.InstituteRepository;

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
     * @return list of all institutes
     * @author Leon Chemnitz
     */
    public List<Institute> getAll() {
        // TODO: implement authentication
        return instituteRepository.findAll();
    }

    /**
     * Get a {@link Institute} based on its Id. Only returns {@link Institute}s the
     * active User is authenticated to see.
     * 
     * @param id Id to search for
     * @return found Institute with matching Id, null if none is found
     * @author Leon Chemnitz
     */
    public Institute getById(String id) {
        // TODO: implement authentication
        Optional<Institute> foundInstitute = instituteRepository.findById(id);

        if (foundInstitute.isPresent()) {
            return foundInstitute.get();
        } else {
            LOGGER.warn("Tried to get Institute which doesn't exist in Database! Institute ID was: " + id);
            return null;
        }
    }

    /**
     * Persist one Institute
     *
     * @param institute to persist
     * @author Leon Chemnitz
     */
    public void save(Institute institute) {
        // TODO: Data Validation
        instituteRepository.save(institute);
    }

    /**
     * Update one Institute in Database
     * 
     * @param institute to update
     * @author Leon Chemnitz
     */
    public void update(Institute institute) {
        // TODO: Data Validation
        instituteRepository.save(institute);
    }

    /**
     * Delete a Institute
     *
     * @param institute to delete
     * @author Leon Chemnitz
     */
    public void deleteInstitute(Institute institute) {
        instituteRepository.delete(institute);
    }
}