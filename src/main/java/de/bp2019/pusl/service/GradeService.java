package de.bp2019.pusl.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.util.LimitOffsetPageRequest;

/**
 * Service providing relevant Grades
 *
 * @author Luca Dinies, Leon Chemnitz
 */

@Service
public class GradeService extends AbstractDataProvider<Grade, String>{
    private static final long serialVersionUID = -8681198334128727062L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeService.class);

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    UserRepository userRepository;

    public GradeService() {
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(Query<Grade, String> query) {
        return (int) gradeRepository.count();
    }

    @Override
    public Stream<Grade> fetch(Query<Grade, String> query) {
        Pageable pageable = new LimitOffsetPageRequest( query.getLimit(), query.getOffset());
        return gradeRepository.findAll(pageable).stream();
    }

    /**
     * Get all Grades.
     *
     * @return list of all grades
     * @author Luca Dinies
     */
    public List<Grade> getAll() {
        return getAll(null);
    }

    /**
     * Get all Grades.
     *
     * @return list of all {@link Grade}s
     * @author Leon Chemnitz
     */
    public List<Grade> getAll(Grade filter) {
        if(filter == null){
            filter = new Grade();
        }        
        // filter.setLectureRef(filter.getLectureEmb());
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("matrNumber", GenericPropertyMatchers.contains())
                .withIgnorePaths("lecture", "lectureEmb")
                .withIgnoreNullValues();

        return gradeRepository.findAll(Example.of(filter, matcher));
    }

    /**
     * Save one Grade in Database
     *
     * @param grade to Save
     * @author Luca Dinies
     */
    public void save(Grade grade) {
        gradeRepository.save(grade);
    }

    /**
     * Update one Grade in Database
     *
     * @param grade to update
     * @author Luca Dinies
     */
    public void update(Grade grade) {
        gradeRepository.save(grade);
    }

    /**
     * Delete one Grade in Database
     *
     * @param grade to delete
     * @author Luca Dinies
     */
    public void delete(Grade grade) {
        gradeRepository.delete(grade);
    }

    /**
     * Get a Grade based on its id.
     *
     * @param id to Search for
     * @author Luca Dinies
     */
    public Grade findGradeById(String id) {
        Optional<Grade> foundGrade = gradeRepository.findById(id);

        if (foundGrade.isPresent()) {
            return foundGrade.get();
        } else {
            LOGGER.warn("Tried to get Grade which doesn't exist in Database! Grade ID was: " + id);
            return null;
        }

    }
}
