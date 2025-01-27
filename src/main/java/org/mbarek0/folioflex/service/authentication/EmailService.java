package org.mbarek0.folioflex.service.authentication;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String email, String verificationToken,String clientOrigin) {
        String verificationUrl = clientOrigin + "/verify-email?token=" + verificationToken;

        sendMail(
            email,
            "Verify Your Email",
            "To confirm your account, please click the link below:\n" + verificationUrl
        );
    }

    public void sendPasswordResetEmail(String email, String resetToken, String clientOrigin) {
        String resetLink = clientOrigin +"/reset-password?token=" + resetToken;

        sendMail(
             email,
            "Reset Your Password",
            "To reset your password, please click the link below:\n" + resetLink
        );


    }

    public void sendMail(String email, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailSender.send(mailMessage);
    }
}