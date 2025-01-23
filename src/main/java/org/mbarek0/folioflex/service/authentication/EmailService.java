package org.mbarek0.folioflex.service.authentication;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String baseUrl ;

    public EmailService(JavaMailSender mailSender, @Value("${app.base.url}") String baseUrl) {
        this.mailSender = mailSender;
        this.baseUrl = baseUrl;
    }

    public void sendVerificationEmail(String email, String verificationToken) {
        String verificationUrl = baseUrl + "/api/auth/verify?token=" + verificationToken;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Verify Your Email");
        mailMessage.setText("To confirm your account, please click the link below:\n" + verificationUrl);

        mailSender.send(mailMessage);
    }
}