package de.bp2019.zentraldatei.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.module.Module;
import de.bp2019.zentraldatei.repository.ExerciseSchemeRepository;
import de.bp2019.zentraldatei.repository.InstituteRepository;
import de.bp2019.zentraldatei.repository.ModuleRepository;
import de.bp2019.zentraldatei.repository.UserRepository;

/**
 * Service providing relevant {@link Module}s
 *
 * @author Leon Chemnitz
 */
@Service
public class ModuleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleService.class);

    @Autowired
    ModuleRepository moduleRepository;
    @Autowired
    InstituteRepository instituteRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    public ModuleService() {
    }

    /**
     * Get a Module based on its Id. Only return Modules the User is authenticated
     * to see.
     *
     * @param id Id to search for
     * @return found Module with maching Id, null if none is found
     * @author Leon Chemnitz
     */
    public Module getModuleById(String id) {
        // TODO: Implement Authentication

        Optional<Module> foundModule = moduleRepository.findById(id);

        if (foundModule.isPresent()) {
            return foundModule.get();
        } else {
            return null;
        }
    }

    /**
     * Get all Modules the User is authenticated to see.
     *
     * @return list of al module schemes
     * @author Leon Chemnitz
     */
    public List<Module> getAllModules() {
        // TODO: Implement Authentication
        return moduleRepository.findAll();
    }

    /**
     * Persist one Module
     *
     * @param module to persist
     * @author Leon Chemnitz
     */
    public void saveModule(Module module) {
        // TODO: Data Validation
        moduleRepository.save(module);
    }

    /**
     * Delete a Module
     *
     * @param module to delete
     * @author Luca Dinies
     */
    public void deleteModule(Module module) {
        moduleRepository.delete(module);
    }
}