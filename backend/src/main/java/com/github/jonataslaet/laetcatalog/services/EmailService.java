package com.github.jonataslaet.laetcatalog.services;

import com.github.jonataslaet.laetcatalog.controllers.dtos.SendingEmailDTO;
import com.github.jonataslaet.laetcatalog.services.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(SendingEmailDTO sendingEmailDTO) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(emailFrom);
            simpleMailMessage.setTo(sendingEmailDTO.getTo());
            simpleMailMessage.setSubject(sendingEmailDTO.getSubject());
            simpleMailMessage.setText(sendingEmailDTO.getBody());
            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            throw new EmailException("Failed to send email");
        }
    }
}
