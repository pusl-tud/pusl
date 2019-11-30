package de.bp2019.zentraldatei.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.Repository;

import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.enums.UserType;

import java.util.List;

interface UserRepository extends Repository<User, Long>{
	
	List<User> findByFirstName(String firstName);
	List<User> findByLastName(String lastName);
	List<User> findByType(UserType type);
}