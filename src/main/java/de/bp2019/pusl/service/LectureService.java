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
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.LectureRepository;
import de.bp2019.pusl.util.LimitOffsetPageRequest;
import de.bp2019.pusl.util.Utils;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * Service providing relevant {@link Lecture}s
 *
 * @author Leon Chemnitz
 */
@Service
@Scope("session")
public class LectureService extends AbstractDataProvider<Lecture, String> {
    private static final long serialVersionUID = -4325095502734352010L;

    private static final Logger LOGGER = LoggerFactory.getLogger(LectureService.class);

    @Autowired
    LectureRepository lectureRepository;
    
    @Autowired
    AuthenticationService authenticationService;

    /**
     * Get a {@link Lecture} based on its Id. Only return {@link Lecture}s the
     * active User is authenticated to see.
     *
     * @param id to search for
     * @return found {@link Lecture} with maching Id
     * @author Leon Chemnitz
     * @throws DataNotFoundException
     * @throws UnauthorizedException
     */
    public Lecture getById(String id) throws DataNotFoundException, UnauthorizedException {
        LOGGER.info("checking if lecture with id " + id + " is present");
        Optional<Lecture> foundLecture = lectureRepository.findById(id);

        if (foundLecture.isEmpty()) {
            LOGGER.info("not found in database");
            throw new DataNotFoundException();
        } else {

            LOGGER.info("found in database");
            Lecture lecture = foundLecture.get();
            LOGGER.debug(lecture.toString());

            if (userIsAuthorized(lecture)) {
                LOGGER.info("returned because user is authorized");
                return lecture;
            } else {
                LOGGER.info("user is not authorized");
                throw new UnauthorizedException();
            }
        }
    }

    /**
     * Persist one {@link Lecture}
     * 
     * @param lecture to persist
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void save(Lecture lecture) throws UnauthorizedException {
        LOGGER.info("saving lecture");
        LOGGER.debug(lecture.toString());

        if (userIsAuthorized(lecture)) {
            lectureRepository.save(lecture);
        }else{
            LOGGER.error("user is not authorized to save lecture!");
            throw new UnauthorizedException();
        }

    }

    /**
     * Delete a {@link Lecture}
     *
     * @param lecture to delete
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void delete(Lecture lecture) throws UnauthorizedException {
        LOGGER.info("deleting lecture");
        LOGGER.debug(lecture.toString());

        if (userIsAuthorized(lecture)) {
            lectureRepository.delete(lecture);
        }else{
            LOGGER.error("user is not authorized to delete lecture!");
            throw new UnauthorizedException();
        }

    }

    /**
     * Check if current user is authorized to access the {@link Lecture}
     * 
     * @param lecture
     * @return
     * @author Leon Chemnitz
     */
    private boolean userIsAuthorized(Lecture lecture) {
        User currentUser = authenticationService.currentUser();
        LOGGER.info(currentUser.toString());

        switch (currentUser.getType()) {
            default:
            case HIWI:
            case WIMI:
                break;
            case ADMIN:
                if (!Utils.containsAny(currentUser.getInstitutes(), lecture.getInstitutes()))
                    break;
                return true;
            case SUPERADMIN:
                return true;
        }
        return false;
    }

    /**
     * Check wether a {@link Lecture} with a given name already exists in Database.
     * Also takes an id parameter which excludes the entity with matching Id from
     * the check. This is neccessairy for updating an existing {@link Lecture}.
     * 
     * @param name
     * @param id
     * @return
     * @author Leon Chemnitz
     */
    public boolean checkNameAvailable(String name, Optional<ObjectId> id) {
        LOGGER.info("checking if lecture name '" + name + "' exists in database");

        Optional<Lecture> foundLecture = lectureRepository.findByName(name);

        if (!foundLecture.isPresent()) {
            LOGGER.info("name is available because no lecture with name '" + name + "' was found");
            return true;
        }

        ObjectId foundId = foundLecture.get().getId();

        if (id.isPresent() && foundId.equals(id.get())) {
            LOGGER.info("name is available because it's the name of the current lecture");
            return true;
        } else {
            LOGGER.info("name is already taken by lecture with id " + foundId.toString());
            return false;
        }

    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(Query<Lecture, String> query) {
        User currentUser = authenticationService.currentUser();

        if (currentUser.getType() == UserType.SUPERADMIN) {
            return (int) lectureRepository.count();
        }
        return lectureRepository.countByInstitutesIn(currentUser.getInstitutes());
    }

    @Override
    public Stream<Lecture> fetch(Query<Lecture, String> query) {
        Pageable pageable = new LimitOffsetPageRequest(query.getLimit(), query.getOffset());        
        User currentUser = authenticationService.currentUser();

        if (currentUser.getType() == UserType.SUPERADMIN) {
            return lectureRepository.findAll(pageable).stream();
        }
        return lectureRepository.findByInstitutesIn(currentUser.getInstitutes(), pageable);
    }
}