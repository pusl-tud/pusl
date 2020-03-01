package de.bp2019.pusl.config;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;

@Component
public class TestUtils {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TestProperties testProperties;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public void authenticateAs(UserType userType) throws AuthenticationException {
        userRepository.deleteAll();

        User mockUser = new User();
        mockUser.setEmailAddress(testProperties.getSuperadminUsername());
        mockUser.setPassword(passwordEncoder.encode(testProperties.getSuperadminPassword()));
        mockUser.setType(UserType.SUPERADMIN);
        userRepository.save(mockUser);

        mockUser = new User();
        mockUser.setEmailAddress(testProperties.getAdminUsername());
        mockUser.setPassword(passwordEncoder.encode(testProperties.getAdminPassword()));
        mockUser.setType(UserType.ADMIN);
        userRepository.save(mockUser);

        mockUser = new User();
        mockUser.setEmailAddress(testProperties.getWimiUsername());
        mockUser.setPassword(passwordEncoder.encode(testProperties.getWimiPassword()));
        mockUser.setType(UserType.WIMI);
        userRepository.save(mockUser);

        mockUser = new User();
        mockUser.setEmailAddress(testProperties.getHiwiUsername());
        mockUser.setPassword(passwordEncoder.encode(testProperties.getHiwiPassword()));
        mockUser.setType(UserType.HIWI);
        userRepository.save(mockUser);

        Authentication authentication;

        switch (userType) {
            case SUPERADMIN:
                authentication = new UsernamePasswordAuthenticationToken(testProperties.getSuperadminUsername(),
                        testProperties.getSuperadminPassword());
                        break;
            case ADMIN:
                authentication = new UsernamePasswordAuthenticationToken(testProperties.getAdminUsername(),
                        testProperties.getAdminPassword());
                        break;
            case WIMI:
                authentication = new UsernamePasswordAuthenticationToken(testProperties.getWimiUsername(),
                        testProperties.getWimiPassword());
                        break;
            case HIWI:
                authentication = new UsernamePasswordAuthenticationToken(testProperties.getHiwiUsername(),
                        testProperties.getHiwiPassword());
                        break;
            default: throw new AuthenticationException("there were Errors during authentication");
        }

        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userRepository.deleteAll();
    }
}