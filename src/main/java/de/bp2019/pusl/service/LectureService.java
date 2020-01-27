package de.bp2019.pusl.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.LectureRepository;
import de.bp2019.pusl.repository.UserRepository;

/**
 * Service providing relevant {@link Lecture}s
 *
 * @author Leon Chemnitz
 */
@Service
public class LectureService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LectureService.class);

    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    InstituteRepository instituteRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    public LectureService() {
    }

    /**
     * Get a {@link Lecture} based on its Id. Only return {@link Lecture}s the
     * active User is authenticated to see.
     *
     * @param id to search for
     * @return found {@link Lecture} with maching Id, null if none is found
     * @author Leon Chemnitz
     */
    public Lecture getById(String id) {
        // TODO: Implement Authentication

        Optional<Lecture> foundLecture = lectureRepository.findById(id);

        if (foundLecture.isPresent()) {
            return foundLecture.get();
        } else {
            return null;
        }
    }

    /**
     * Get all {@link Lecture}s the active User is authenticated to see.
     *
     * @return list of all {@link Lecture}s
     * @author Leon Chemnitz
     */
    public List<Lecture> getAll() {
        // TODO: Implement Authentication
        return lectureRepository.findAll();
    }

    /**
     * Persist one {@link Lecture}
     * 
     * @param lecture to persist
     * @author Leon Chemnitz
     */
    public void save(Lecture lecture) {
        // TODO: Data Validation
        // TODO: Implement Authentication
        lectureRepository.save(lecture);
        LOGGER.debug("Saved one Lecture");
    }

    /**
     * Delete a {@link Lecture}
     *
     * @param lecture to delete
     * @author Luca Dinies
     */
    public void delete(Lecture lecture) {
        // TODO: Implement Authentication
        lectureRepository.delete(lecture);
    }
}