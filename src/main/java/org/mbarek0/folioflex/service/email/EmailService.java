package org.mbarek0.folioflex.service.email;


public interface EmailService {

     void sendVerificationEmail(String email, String verificationToken,String clientOrigin);

     void sendPasswordResetEmail(String email, String resetToken, String clientOrigin);

     void sendMail(String email, String subject, String body);
}
