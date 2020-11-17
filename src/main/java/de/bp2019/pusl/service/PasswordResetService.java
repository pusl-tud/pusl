package de.bp2019.pusl.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.vaadin.flow.component.UI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.PasswordResetToken;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.PasswordResetTokenRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.views.LoginView;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;

@Service
public class PasswordResetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetService.class);

    private static final int EXPIRATION_MINUTES = 5;
    private static final int CLEANUP_INTERVAL = 1000 * 60 * 60 * 2; //in ms -> 2h

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    PasswordEncoder passwordEncoder;

    public void sendActivationCode(String email) {
        Optional<User> maybeUser = userRepository.findByEmailAddress(email);

        if (maybeUser.isEmpty()) {
            return;
        }

        User user = maybeUser.get();

        cleanupTokens();

        Optional<PasswordResetToken> resetToken = passwordResetTokenRepository.findByUser(user.getId());

        if (resetToken.isPresent()) {
            return;
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user.getId());
        passwordResetTokenRepository.save(passwordResetToken);

        String message = "Ihr angeforderter Aktivierungscode lautet: " + token;
        emailService.sendSimpleMessage(user.getEmailAddress(), "PUSL Passwort Reset", message);
    }

    @Scheduled(fixedRate = CLEANUP_INTERVAL)
    private void cleanupTokens() {
        LOGGER.info("Cleaning up passwort reset tokens");
        List<PasswordResetToken> tokens = passwordResetTokenRepository.findAll();
        LocalDateTime expirationDate = LocalDateTime.now().minusMinutes(EXPIRATION_MINUTES);

        tokens.forEach(token -> {
            if (token.getCreationDate().isBefore(expirationDate)) {
                LOGGER.info("deleting expired token for user with ID: " + token.getUser());
                passwordResetTokenRepository.delete(token);
            }
        });
    }

    public boolean isTokenValid(String email, String token) {
        Optional<User> maybeUser = userRepository.findByEmailAddress(email);

        if (maybeUser.isEmpty()) {
            return false;
        }

        User user = maybeUser.get();

        cleanupTokens();

        Optional<PasswordResetToken> maybeResetToken = passwordResetTokenRepository.findByUser(user.getId());

        if (maybeResetToken.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = maybeResetToken.get();

        if (resetToken.getToken().equals(token)) {
            return true;
        }

        return false;
    }

    public void setNewPassword(String email, String password, String activationCode) {
        try {
            Optional<User> maybeUser = userRepository.findByEmailAddress(email);

            if (maybeUser.isEmpty()){
                throw new DataNotFoundException();
            }

            User user = maybeUser.get();
            Optional<PasswordResetToken> maybeToken = passwordResetTokenRepository.findByUser(user.getId());

            if (maybeToken.isEmpty()){
                throw new DataNotFoundException();
            }

            PasswordResetToken token = maybeToken.get();

            if(!token.getToken().equals(activationCode)){
                throw new IllegalStateException();
            }

            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);

            passwordResetTokenRepository.delete(token);

            
            UI.getCurrent().navigate(LoginView.class);
            SuccessDialog.open("Passwort wurde erfolgreich zurück gesetzt");
        } catch (Exception e) {
            UI.getCurrent().navigate(LoginView.class);
            ErrorDialog.open("Fehler beim zurücksetzen des Passwortes.\nPasswort wurde nicht zurück gesetzt.");
        }
    }
}
