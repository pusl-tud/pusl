package de.bp2019.pusl.service;

import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.GradeFilter;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.util.LimitOffsetPageRequest;
import de.bp2019.pusl.util.Utils;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * Service providing relevant Grades
 *
 * @author Leon Chemnitz
 */
@Service
@Scope("session")
public class GradeService extends AbstractDataProvider<Grade, String> {
    private static final long serialVersionUID = -8681198334128727062L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeService.class);

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * Save one Grade
     *
     * @param user to persist
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void save(Grade grade) throws UnauthorizedException {
        LOGGER.info("saving grade");
        LOGGER.debug(grade.toString());

        if (userIsAuthorized(grade)) {
            gradeRepository.save(grade);
        } else {
            LOGGER.error("user is not authorized to access Grade!");
            throw new UnauthorizedException();
        }
    }

    /**
     * Delete one Grade
     *
     * @param grade to delete
     * @author Leon Chemnitz
     * @throws UnauthorizedException
     */
    public void delete(Grade grade) throws UnauthorizedException {
        LOGGER.info("deleting grade");
        LOGGER.debug(grade.toString());

        if (userIsAuthorized(grade)) {
            gradeRepository.delete(grade);
        } else {
            LOGGER.error("user is not authorized to access Grade!");
            throw new UnauthorizedException();
        }
    }

    /**
     * Get a {@link Grade} based on its Id. Only returns Grades the active User is
     * authenticated to see.
     * 
     * @param id Id to search for
     * @return found Grade with matching Id, null if none is found
     * @author Leon Chemnitz
     * @throws DataNotFoundException
     * @throws UnauthorizedException
     */
    public Grade getById(String id) throws DataNotFoundException, UnauthorizedException {
        LOGGER.info("checking if grade with id " + id + " is present");
        Optional<Grade> foundGrade = gradeRepository.findById(id);

        if (foundGrade.isEmpty()) {
            LOGGER.info("not found in database");
            throw new DataNotFoundException();
        } else {

            LOGGER.info("found in database");
            Grade grade = foundGrade.get();
            LOGGER.debug(grade.toString());

            if (userIsAuthorized(grade)) {
                LOGGER.info("returned because user is authorized");
                return grade;
            } else {
                LOGGER.error("user is not authorized to access User!");
                throw new UnauthorizedException();
            }
        }
    }

    /**
     * Check if current user is authorized to access the {@link Grade}
     * 
     * @param grade
     * @return
     * @author Leon Chemnitz
     */
    private boolean userIsAuthorized(Grade grade) {
        Set<Institute> institutes = grade.getLecture().getInstitutes();
        User currentUser = authenticationService.currentUser();

        switch (currentUser.getType()) {
            default:
            case HIWI:
                if (!grade.getExercise().isAssignableByHIWI()
                        || !grade.getLecture().getHasAccess().contains(currentUser.getId()))
                    break;
            case WIMI:
            case ADMIN:
                if (!currentUser.getInstitutes().containsAll(institutes))
                    break;
                return true;
            case SUPERADMIN:
                return true;
        }
        return false;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(Query<Grade, String> query) {
        return size(query, null);
    }

    public int size(Query<Grade, String> query, GradeFilter filter) {
        LOGGER.debug("counting Grades");
        return (int) mongoTemplate.count(query(buildCriteria(filter)), Grade.class);
    }

    @Override
    public Stream<Grade> fetch(Query<Grade, String> query) {
        return fetch(query, null);
    }

    public Stream<Grade> fetch(Query<Grade, String> query, GradeFilter filter) {
        LOGGER.debug("fetching Grades");
        Pageable pageable = new LimitOffsetPageRequest(query.getLimit(), query.getOffset());

        List<Order> order = query.getSortOrders().stream().map(so -> {
            if (so.getDirection() == SortDirection.ASCENDING) {
                LOGGER.info("asc " + so.getSorted());
                return Order.asc(so.getSorted());
            } else {
                LOGGER.info("desc " + so.getSorted());
                return Order.desc(so.getSorted());
            }
        }).collect(Collectors.toList());
        Sort sort = Sort.by(order);

        var mongoQuery = query(buildCriteria(filter)).with(sort).with(pageable);

        return mongoTemplate.find(mongoQuery, Grade.class).stream();
    }

    /**
     * Build efficient Query criteria matching the filter
     * 
     * @param filter
     * @return
     * @author Leon Chemnitz
     */
    private Criteria buildCriteria(GradeFilter filter) {
        if (filter == null) {
            filter = new GradeFilter();
        }

        Criteria criteria = new Criteria();

        Date startDate = filter.getStartDate();
        Date endDate = filter.getEndDate();
        if (startDate != null || endDate != null) {
            criteria = criteria.and("handIn");
            if (startDate != null) {
                criteria = criteria.gte(startDate);
            }

            if (endDate != null) {
                criteria = criteria.lte(endDate);
            }
        }

        String matrNumber = filter.getMatrNumber();
        if (matrNumber != null) {
            criteria = criteria.and("matrNumber").regex(".*" + matrNumber + ".*");
        }

        Lecture lecture = filter.getLecture();
        if (lecture != null) {
            criteria = criteria.and("lecture._id").is(lecture.getId());

            Exercise exercise = filter.getExercise();
            if (exercise != null) {
                criteria = criteria.and("exercise.name").is(exercise.getName());

                String grade = filter.getGrade();
                if (grade != null) {
                    criteria = criteria.and("value").is(grade);
                }
            }
        }

        User currentUser = authenticationService.currentUser();
        switch (currentUser.getType()) {
            default:
            case HIWI:
                criteria = criteria.and("exercise.assignableByHIWI").is(true);
                criteria = criteria.and("lecture.hasAccess").in(currentUser.getId());
            case WIMI:
            case ADMIN:
                criteria = criteria.and("lecture.institutes").in(currentUser.getInstitutes());
            case SUPERADMIN:
        }

        return criteria;
    }

    /**
     * Check if MatrNumber is valid with the validation algorithm of TU Darmstadt
     * 
     * @param grade
     * @return
     * @author Leon Chemnitz
     */
    public static boolean gradeIsValid(Grade grade) {
        if (!Utils.isMatrNumber(grade.getMatrNumber())) {
            ErrorDialog.open("Matrikelnummer ist fehlerhaft");
            return false;
        }

        Lecture lecture = grade.getLecture();
        if (lecture == null) {
            ErrorDialog.open("Bitte Veranstaltung angeben");
            return false;
        }

        Exercise exercise = grade.getExercise();
        if (exercise == null || !lecture.getExercises().contains(exercise)) {
            ErrorDialog.open("Bitte Übung angeben");
            return false;
        }

        String value = grade.getValue();
        if (value == null || value.equals("")) {
            SuccessDialog.open("Kein Notenwert angegeben, Standardwert wird gesetzt");

            ExerciseScheme exerciseScheme = exercise.getScheme();

            if (exerciseScheme.isNumeric()) {
                grade.setValue(Double.toString(exerciseScheme.getDefaultValueNumeric()));
            } else {
                grade.setValue(exerciseScheme.getDefaultValueToken().getName());
            }

            return true;
        }

        return true;
    }
}