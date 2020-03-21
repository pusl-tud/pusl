package de.bp2019.pusl.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;

/**
 * Repository for access of {@link User}s
 * 
 * @author Leon Chemnitz
 */
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmailAddress(String emailAddress);
    List<User> findByType(UserType type);
    Stream<User> findByInstitutesIn(Set<Institute> institutes, Pageable pageable);
    Stream<User> findByType(UserType type, Pageable pageable);
    Stream<User> findByInstitutesInAndType(Set<Institute> institutes, UserType type, Pageable pageable);
    int countByInstitutesIn(Set<Institute> institutes);
    int countByType(UserType type);
    int countByInstitutesInAndType(Set<Institute> institutes, UserType type);
}