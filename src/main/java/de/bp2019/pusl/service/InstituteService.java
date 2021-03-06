package de.bp2019.pusl.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.Query;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.model.interfaces.BelongsToInstitutes;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.util.LimitOffsetPageRequest;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * Service providing relevant {@link Institute}s
 * 
 * @author Leon Chemnitz
 */
@Service
@Scope("session")
public class InstituteService extends AbstractDataProvider<Institute, String> {
    private static final long serialVersionUID = -1382092534461892569L;

    private static final Logger LOGGER = LoggerFactory.getLogger(InstituteService.class);

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    AuthenticationService authenticationService;

    /**
     * Get a {@link Institute} based on its Id. Only returns {@link Institute}s the
     * active User is authorized to see.
     * 
     * @param id Id to search for
     * @return found Institute with matching Id
     * @author Leon Chemnitz
     * @throws UnauthorizedException if user not authorized to access
     * @throws DataNotFoundException if entity not found in database
     */
    public Institute getById(ObjectId id) throws UnauthorizedException, DataNotFoundException {
        LOGGER.info("checking if institute with id " + id + " is present");
        Optional<Institute> foundInstitute = instituteRepository.findById(id);

        if (foundInstitute.isEmpty()) {
            LOGGER.info("not found in database");
            throw new DataNotFoundException();
        } else {
            LOGGER.info("found in database");

            Institute institute = foundInstitute.get();
            LOGGER.debug(institute.toString());
            User currentUser = authenticationService.currentUser();

            if (currentUser.getType() == UserType.SUPERADMIN || (currentUser.getType() == UserType.ADMIN
                    && currentUser.getInstitutes().contains(institute.getId()))) {
                LOGGER.info("returned because user is authorized");
                return institute;
            } else {
                LOGGER.error("user is not authorized");
                throw new UnauthorizedException();
            }
        }
    }

    /**
     * Persist one Institute
     *
     * @param institute to persist
     * @author Leon Chemnitz
     * @throws UnauthorizedException if user not authorized to access
     */
    public void save(Institute institute) throws UnauthorizedException {
        LOGGER.info("saving institute");
        LOGGER.debug(institute.toString());

        if (userIsAuthorized(institute)) {
            instituteRepository.save(institute);
        } else {
            LOGGER.error("user is not authorized to access Institute!");
            throw new UnauthorizedException();
        }
    }

    /**
     * Delete a Institute
     *
     * @param institute to delete
     * @author Leon Chemnitz
     * @throws UnauthorizedException if user not authorized to access
     */
    public void delete(Institute institute) throws UnauthorizedException {
        LOGGER.info("deleting institute");
        LOGGER.debug(institute.toString());

        if (userIsAuthorized(institute)) {
            instituteRepository.delete(institute);
        } else {
            LOGGER.error("user is not authorized to access Institute!");
            throw new UnauthorizedException();
        }
    }

    /**
     * Check if current user is authorized to access the {@link Institute}
     * 
     * @param institute entity to check
     * @return true if authorized
     * @author Leon Chemnitz
     */
    private boolean userIsAuthorized(Institute institute) {
        UserType userType = authenticationService.currentUserType();
        LOGGER.debug(userType.toString());
        switch (userType) {
            default:
            case HIWI:
            case WIMI:
            case ADMIN:
                break;
            case SUPERADMIN:
                return true;
        }
        return false;
    }

    /**
     * Check wether a {@link Institute} with a given name already exists in
     * Database. Also takes an id parameter which excludes the entity with matching
     * Id from the check. This is neccessairy for updating an existing
     * {@link Institute}.
     * 
     * @param name name to check
     * @param id   id of current User
     * @return true if available
     * @author Leon Chemnitz
     */
    public boolean checkNameAvailable(String name, Optional<ObjectId> id) {
        LOGGER.info("checking if institute name '" + name + "' exists in database");

        Optional<Institute> foundInstitute = instituteRepository.findByName(name);

        if (!foundInstitute.isPresent()) {
            LOGGER.info("name is available because no institute with name '" + name + "' was found");
            return true;
        }

        ObjectId foundId = foundInstitute.get().getId();

        if (id.isPresent() && foundId.equals(id.get())) {
            LOGGER.info("name is available because it's the name of the current institute");
            return true;
        } else {
            LOGGER.info("name is already taken by institute with id " + foundId.toString());
            return false;
        }

    }

    /**
     * @author Leon Chemnitz
     */
    @Override
    public boolean isInMemory() {
        return false;
    }

    /**
     * @author Leon Chemnitz
     */
    @Override
    public int size(Query<Institute, String> query) {
        User currentUser = authenticationService.currentUser();

        if (currentUser.getType() == UserType.SUPERADMIN) {
            return (int) instituteRepository.count();
        }
        return currentUser.getInstitutes().size();
    }

    /**
     * @author Leon Chemnitz
     */
    @Override
    public Stream<Institute> fetch(Query<Institute, String> query) {
        Pageable pageable = new LimitOffsetPageRequest(query.getLimit(), query.getOffset());
        User currentUser = authenticationService.currentUser();

        if (currentUser.getType() == UserType.SUPERADMIN) {
            return instituteRepository.findAll(pageable).stream();
        }
        return instituteRepository.findByIdIn(currentUser.getInstitutes(), pageable);
    }

    public Set<Institute> getInstitutesFromObject(BelongsToInstitutes object) {
        if (object == null || object.getInstitutes() == null) {
            return new HashSet<>();
        }

        LOGGER.info(object.getInstitutes().toString());
        Set<Institute> institutes = instituteRepository.findAllByIdIn(object.getInstitutes());
        LOGGER.info(institutes.toString());
        return institutes;
    }

    public void setInstitutesToObject(BelongsToInstitutes object, Set<Institute> institutes) {
        Set<ObjectId> ids = institutes.stream().map(Institute::getId).collect(Collectors.toSet());
        object.setInstitutes(ids);
    }
}