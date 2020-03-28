package de.bp2019.pusl.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;

/**
 * Configure Spring UserDetailsService. Needed for Spring Security
 * 
 * @author Leon Chemnitz
 */
@Configuration
public class UserDetailsConfig implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByEmailAddress(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return user.get();
    }
}