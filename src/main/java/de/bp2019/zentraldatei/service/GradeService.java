package de.bp2019.zentraldatei.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.zentraldatei.model.Grade;
import de.bp2019.zentraldatei.repository.ExerciseSchemeRepository;
import de.bp2019.zentraldatei.repository.GradeRepository;
import de.bp2019.zentraldatei.repository.ModuleRepository;
import de.bp2019.zentraldatei.repository.UserRepository;

/**
 * Service providing relevant Grades
 *
 * @author Luca Dinies
 */

@Service
public class GradeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradeService.class);

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    UserRepository userRepository;

    public GradeService() {
    }

    /**
     * Get all Grades.
     *
     * @return list of all grades
     * @author Luca Dinies
     */
    public List<Grade> getAllGrades(){
        return gradeRepository.findAll();
    }

    /**
     * Save one Grade in Database
     *
     * @param grade to Save
     * @author Luca Dinies
     */
    public void save(Grade grade){
        gradeRepository.save(grade);
    }

    /**
     * Update one Grade in Database
     *
     * @param grade to update
     * @author Luca Dinies
     */
    public void update(Grade grade){
        gradeRepository.save(grade);
    }

    /**
     * Delete one Grade in Database
     *
     * @param grade to delete
     * @author Luca Dinies
     */
    public void delete(Grade grade){
        gradeRepository.delete(grade);
    }

    /**
     * Get a Grade based on its id.
     *
     * @param id to Search for
     * @author Luca Dinies
     */
    public Grade findGradeById(String id){
        Optional<Grade> foundGrade = gradeRepository.findById(id);

        if(foundGrade.isPresent()){
            return foundGrade.get();
        } else {
            LOGGER.warn("Tried to get Grade which doesn't exist in Database! Grade ID was: " + id);
            return null;
        }

    }
}
