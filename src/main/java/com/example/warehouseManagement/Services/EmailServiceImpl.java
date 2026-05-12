package com.example.warehouseManagement.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailServiceImpl(JavaMailSender mailSender,
                            @Value("${app.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendTwoFactorCode(String to, String code) {
        String subject = "Your verification code";
        String body = "Your verification code is: " + code
                + "\n\nThis code will expire shortly. If you did not try to sign in, ignore this email.";
        send(to, subject, body);
    }

    @Override
    public void sendPasswordResetLink(String to, String resetUrl) {
        String subject = "Reset your password";
        String body = "We received a request to reset your password.\n"
                + "Click the link below to choose a new one:\n\n"
                + resetUrl
                + "\n\nIf you did not request this, you can ignore this email.";
        send(to, subject, body);
    }

    private void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
            log.info("Email sent to {} subject=\"{}\"", to, subject);
        } catch (MailException e) {
            // Dev fallback: log the full message so the developer can grab the code/link
            // without having SMTP configured. In production set up SMTP via env vars.
            log.warn("Failed to send email via SMTP ({}). Falling back to console output.", e.getMessage());
            log.info("---- EMAIL (dev fallback) ----\nTo: {}\nSubject: {}\n\n{}\n------------------------------",
                    to, subject, body);
        }
    }
}
