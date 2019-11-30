package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.Repository;

import de.bp2019.zentraldatei.model.ModuleScheme;

import java.util.List;
import java.util.Date;

interface ModuleSchemeRepository extends Repository<ModuleScheme, Long>{
	
	List<ModuleScheme> findByName(String name);
	List<ModuleScheme> findByStartDate(Date startDate);
	List<ModuleScheme> findByFinishDate(Date finishDate);
	
	
}