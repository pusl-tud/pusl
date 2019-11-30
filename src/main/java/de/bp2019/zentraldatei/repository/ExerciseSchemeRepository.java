package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.Repository;

import de.bp2019.zentraldatei.model.ExerciseScheme;

import java.util.List;
import java.util.Date;

interface ExerciseSchemeRepository extends Repository<ExerciseScheme, Long>{
	
	List<ExerciseScheme> findByName(String name);
	List<ExerciseScheme> findByStartDate(Date startDate);
	List<ExerciseScheme> findByFinishDate(Date finishDate);
	
}