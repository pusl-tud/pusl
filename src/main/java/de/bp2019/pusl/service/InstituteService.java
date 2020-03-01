package de.bp2019.pusl.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

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

    @Autowired
    UserService userService;

    public InstituteService() {
    }

    /**
     * Get all Institutes the User is authenticated to see.
     * 
     * @return list of all institutes
     * @author Leon Chemnitz
     */
    public List<Institute> getAll() {
        LOGGER.info("getting all institutes");
        if (userService.getCurrentUserType() == UserType.SUPERADMIN) {
            LOGGER.info("returning all because user is SUPERADMIN");
            return instituteRepository.findAll();
        } else {
            LOGGER.info("returning all associated with user");
            return userService.getCurrentUser().getInstitutes().stream().collect(Collectors.toList());
        }
    }

    /**
     * Get a {@link Institute} based on its Id. Only returns {@link Institute}s the
     * active User is authorized to see.
     * 
     * @param id Id to search for
     * @return found Institute with matching Id
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     * @throws DataNotFoundException
     */
    public Institute getById(String id) throws UnauthorizedException, DataNotFoundException {
        LOGGER.info("checking if institute with id " + id + " is present");
        Optional<Institute> foundInstitute = instituteRepository.findById(id);

        if (!foundInstitute.isPresent()) {
            LOGGER.info("not found in database");
            throw new DataNotFoundException();
        } else {
            
            LOGGER.info("found in database");
            Institute institute = foundInstitute.get();

            if (userService.getCurrentUserType() == UserType.SUPERADMIN
                    || userService.getCurrentUser().getInstitutes().contains(foundInstitute.get())) {
                LOGGER.info("returned because user is authorized");
                return institute;
            } else {
                LOGGER.info("user is not authorized");
                throw new UnauthorizedException();
            }
        }
    }

    /**
     * Persist one Institute
     *
     * @param institute to persist
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void save(Institute institute) throws UnauthorizedException {
        LOGGER.info("saving institute with id " + institute.getId().toString());
        if (userService.getCurrentUserType() != UserType.SUPERADMIN) {
            LOGGER.info("user is not authorized!");
            throw new UnauthorizedException();
        }
        instituteRepository.save(institute);
    }

    /**
     * Delete a Institute
     *
     * @param institute to delete
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void deleteInstitute(Institute institute) throws UnauthorizedException {        
        LOGGER.info("deleting institute with id " + institute.getId().toString());
        if (userService.getCurrentUserType() != UserType.SUPERADMIN) {
            LOGGER.info("user is not authorized!");
            throw new UnauthorizedException();
        }
        instituteRepository.delete(institute);
    }

    /**
     * Check wether a Institute with a given name already exists in Database
     * 
     * @param name
     * @return
     * @author Leon Chemnitz
     */
    public boolean checkNameAvailable(String name, ObjectId id) {
        LOGGER.info("checking if institute name '" + name + "' exists in database");

        Optional<Institute> foundInstitute = instituteRepository.findByName(name);

        if(!foundInstitute.isPresent()) {
            LOGGER.info("name is available because no institute with name '" + name + "' was found");
            return true;
        }

        ObjectId foundId = foundInstitute.get().getId();

        if(foundId.equals(id)){
            LOGGER.info("name is available because it's the name of the current institute");
            return true;
        } else {
            LOGGER.info("name is already taken by institute with id " + foundId.toString());
            return false;
        }

    }
}