package de.bp2019.pusl.service;

import java.util.Optional;
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
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.util.LimitOffsetPageRequest;
import de.bp2019.pusl.util.Utils;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * Service providing relevant {@link ExerciseScheme}s
 * 
 * @author Leon Chemnitz
 */
@Service
@Scope("session")
public class ExerciseSchemeService extends AbstractDataProvider<ExerciseScheme, String> {
    private static final long serialVersionUID = 5319757533859168120L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseSchemeService.class);

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;
    
    @Autowired
    AuthenticationService authenticationService;

    /**
     * Get a {@link ExerciseScheme} based on its Id. Only return
     * {@link ExerciseScheme}s the active User is authenticated to see.
     *
     * @param id to search for
     * @return found {@link ExerciseScheme} with maching Id
     * @author Leon Chemnitz
     * @throws DataNotFoundException
     * @throws UnauthorizedException
     */
    public ExerciseScheme getById(String id) throws DataNotFoundException, UnauthorizedException {
        LOGGER.info("checking if exerciseScheme with id " + id + " is present");
        Optional<ExerciseScheme> foundExerciseScheme = exerciseSchemeRepository.findById(id);

        if (foundExerciseScheme.isEmpty()) {
            LOGGER.info("not found in database");
            throw new DataNotFoundException();
        } else {

            LOGGER.info("found in database");
            ExerciseScheme exerciseScheme = foundExerciseScheme.get();
            LOGGER.debug(exerciseScheme.toString());

            if (userIsAuthorized(exerciseScheme)){
                return exerciseScheme;
            } else {
                LOGGER.error("user is not authorized to access Exercise Sheme");
                throw new UnauthorizedException();
            }
        }
    }

    /**
     * Persist one {@link ExerciseScheme}
     * 
     * @param exerciseScheme to persist
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void save(ExerciseScheme exerciseScheme) throws UnauthorizedException {
        LOGGER.info("saving lecture");
        LOGGER.debug(exerciseScheme.toString());

        if (userIsAuthorized(exerciseScheme)) {
            exerciseSchemeRepository.save(exerciseScheme);
        }else{ 
            LOGGER.error("user is not authorized to access ExerciseScheme!");
            throw new UnauthorizedException();
        }
    }

    /**
     * Delete a {@link ExerciseScheme}
     *
     * @param exerciseScheme to delete
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void delete(ExerciseScheme exerciseScheme) throws UnauthorizedException {
        LOGGER.info("deleting lecture");
        LOGGER.debug(exerciseScheme.toString());

        if (userIsAuthorized(exerciseScheme)) {
            exerciseSchemeRepository.delete(exerciseScheme);
        }else{ 
            LOGGER.error("user is not authorized to access ExerciseScheme!");
            throw new UnauthorizedException();
        }
    }

    /**
     * Check if current user is authorized to access the {@link ExerciseScheme}
     * 
     * @param exerciseScheme
     * @return
     * @author Leon Chemnitz
     */
    private boolean userIsAuthorized(ExerciseScheme exerciseScheme) {
        User currentUser = authenticationService.currentUser();

        switch (currentUser.getType()) {
            default:
            case HIWI:
            case WIMI:
                break;
            case ADMIN:
                if (!Utils.containsAny(currentUser.getInstitutes(), exerciseScheme.getInstitutes()))
                    break;
                return true;
            case SUPERADMIN:
                return true;
        }
        return false;
    }

    /**
     * Check wether a {@link ExerciseScheme} with a given name already exists in
     * Database. Also takes an id parameter which excludes the entity with matching
     * Id from the check. This is neccessairy for updating an existing
     * {@link ExerciseScheme}.
     * 
     * @param name
     * @param id
     * @return
     * @author Leon Chemnitz
     */
    public boolean checkNameAvailable(String name, Optional<ObjectId> id) {
        LOGGER.info("checking if exerciseScheme name '" + name + "' exists in database");

        Optional<ExerciseScheme> foundExerciseScheme = exerciseSchemeRepository.findByName(name);

        if (!foundExerciseScheme.isPresent()) {
            LOGGER.info("name is available because no exerciseScheme with name '" + name + "' was found");
            return true;
        }

        ObjectId foundId = foundExerciseScheme.get().getId();

        if (id.isPresent() && foundId.equals(id.get())) {
            LOGGER.info("name is available because it's the name of the current exerciseScheme");
            return true;
        } else {
            LOGGER.info("name is already taken by exerciseScheme with id " + foundId.toString());
            return false;
        }

    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(Query<ExerciseScheme, String> query) {
        User currentUser = authenticationService.currentUser();

        if (currentUser.getType() == UserType.SUPERADMIN) {
            return (int) exerciseSchemeRepository.count();
        }
        return exerciseSchemeRepository.countByInstitutesIn(currentUser.getInstitutes());
    }

    @Override
    public Stream<ExerciseScheme> fetch(Query<ExerciseScheme, String> query) {
        Pageable pageable = new LimitOffsetPageRequest(query.getLimit(), query.getOffset());
        User currentUser = authenticationService.currentUser();

        if (currentUser.getType() == UserType.SUPERADMIN) {
            return exerciseSchemeRepository.findAll(pageable).stream();
        }
        return exerciseSchemeRepository.findByInstitutesIn(currentUser.getInstitutes(), pageable);
    }
}