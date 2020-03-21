package de.bp2019.pusl.service;

import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.LocalDate;
import java.util.List;
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
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.service.dataproviders.GradeFilter;
import de.bp2019.pusl.util.LimitOffsetPageRequest;
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
    UserService userService;

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
     * Check if current user is authorized to access the {@link Grade}
     * 
     * @param grade
     * @return
     * @author Leon Chemnitz
     */
    private boolean userIsAuthorized(Grade grade) {
        Set<Institute> institutes = grade.getLecture().getInstitutes();

        switch (userService.currentUserType()) {
            default:
            case HIWI:
                if (!grade.getExercise().isAssignableByHIWI())
                    break;
            case WIMI:
            case ADMIN:
                if (!userService.currentUserInstitutes().containsAll(institutes))
                    break;
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

        return mongoTemplate.find(query(buildCriteria(filter)).with(sort).with(pageable), Grade.class).stream();
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

        LocalDate startDate = filter.getStartDate();
        if (startDate != null) {
            criteria = criteria.and("handIn").gte(startDate);
        }

        LocalDate endDate = filter.getEndDate();
        if (endDate != null) {
            criteria = criteria.and("handIn").lte(endDate);
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

        User currentUser = userService.currentUser();
        switch (currentUser.getType()) {
            default:
            case HIWI:
                criteria = criteria.and("exercise.assignableByHIWI").is(true);
            case WIMI:
            case ADMIN:
                criteria = criteria.and("lecture.institutes").in(currentUser.getInstitutes());
            case SUPERADMIN:
        }

        return criteria;
    }
}
