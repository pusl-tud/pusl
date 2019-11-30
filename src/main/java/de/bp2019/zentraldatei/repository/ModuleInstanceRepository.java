package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.Repository;

import de.bp2019.zentraldatei.model.ModuleInstance;
import de.bp2019.zentraldatei.model.ModuleScheme;

import java.util.List;
import java.util.Date;

interface ModuleInstanceRepository extends Repository<ModuleInstance, Long>{
	
	List<ModuleInstance> findByScheme(ModuleScheme scheme);
	
}