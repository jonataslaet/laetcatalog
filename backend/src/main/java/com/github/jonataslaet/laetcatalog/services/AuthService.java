package com.github.jonataslaet.laetcatalog.services;

import com.github.jonataslaet.laetcatalog.controllers.dtos.EmailDTO;
import com.github.jonataslaet.laetcatalog.controllers.dtos.NewPasswordDTO;
import com.github.jonataslaet.laetcatalog.controllers.dtos.SendingEmailDTO;
import com.github.jonataslaet.laetcatalog.entities.PasswordRecovery;
import com.github.jonataslaet.laetcatalog.entities.User;
import com.github.jonataslaet.laetcatalog.repositories.PasswordRecoveryRepository;
import com.github.jonataslaet.laetcatalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String passwordRecoveryUri;

    @Autowired
    private PasswordRecoveryRepository passwordRecoveryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createRecoveryToken(EmailDTO emailDTO) {
        User user = userRepository.findByEmail(emailDTO.getEmail());
        if (user != null) {
            String uuidToken = UUID.randomUUID().toString();
            PasswordRecovery passwordRecovery = new PasswordRecovery();
            passwordRecovery.setEmail(emailDTO.getEmail());
            passwordRecovery.setToken(uuidToken);
            passwordRecovery.setExpiration(Instant.now().plusSeconds(60 * tokenMinutes));
            passwordRecoveryRepository.save(passwordRecovery);
            String emailBody = "Click on following link to set a new password: \n" + passwordRecoveryUri + uuidToken +
                    "\n\n This token is valid for the next 30 minutes. Thus, " +
                    "if this email was not requested by you, just ignore it.";
            emailService.sendEmail(new SendingEmailDTO(emailDTO.getEmail(), "Password Recovery", emailBody));
        }
    }

    public void saveNewPassword(String token, NewPasswordDTO newPasswordDTO) {
        List<PasswordRecovery> passwordRecoveries =
                passwordRecoveryRepository.searchValidTokens(token, Instant.now());
        validPasswordRecoveries(passwordRecoveries, newPasswordDTO);
        PasswordRecovery passwordRecovery = passwordRecoveries.get(0);
        User user = userRepository.findByEmail(passwordRecovery.getEmail());
        user.setPassword(passwordEncoder.encode(newPasswordDTO.getNewPassword()));
        passwordRecovery.setExpiration(Instant.now());
        saveUserAndPasswordRecovery(passwordRecovery, user);
    }

    @Transactional
    private void saveUserAndPasswordRecovery(PasswordRecovery passwordRecovery, User user) {
        userRepository.save(user);
        passwordRecoveryRepository.save(passwordRecovery);
    }

    private void validPasswordRecoveries(List<PasswordRecovery> passwordRecoveries, NewPasswordDTO newPasswordDTO) {
        if (passwordRecoveries.isEmpty()) {
            throw new InvalidBearerTokenException("This token is not valid");
        }
        if (!areEquals(newPasswordDTO.getNewPassword(), newPasswordDTO.getNewPasswordConfirmation())) {
            throw new InvalidBearerTokenException("New password and its confirmation have to be the same");
        }
    }

    private boolean areEquals(String word, String wordConfirmation) {
        return word.equals(wordConfirmation);
    }
}
