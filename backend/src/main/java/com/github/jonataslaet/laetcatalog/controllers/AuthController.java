package com.github.jonataslaet.laetcatalog.controllers;

import com.github.jonataslaet.laetcatalog.controllers.dtos.EmailDTO;
import com.github.jonataslaet.laetcatalog.controllers.dtos.NewPasswordDTO;
import com.github.jonataslaet.laetcatalog.controllers.dtos.SendingEmailDTO;
import com.github.jonataslaet.laetcatalog.services.AuthService;
import com.github.jonataslaet.laetcatalog.services.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<Void> sendingEmail(@Valid @RequestBody SendingEmailDTO sendingEmailDTO) {
        emailService.sendEmail(sendingEmailDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recovery-token")
    public ResponseEntity<Void> createRecoveryToken(@Valid @RequestBody EmailDTO emailDTO) {
        authService.createRecoveryToken(emailDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/new-password/{token}")
    public ResponseEntity<Void> saveNewPassword(@PathVariable("token") String token,
        @Valid @RequestBody NewPasswordDTO newPasswordDTO) {
        authService.saveNewPassword(token, newPasswordDTO);
        return ResponseEntity.noContent().build();
    }

}
