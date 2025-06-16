package com.clockin.clockin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * sending email
     * @param email address
     * @param email subject
     * @param email body
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("clockintimemanagementtest@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("Email has sent to: " + to + " with subject: " + subject);
        } catch (MailException e) {
            System.err.println("Failed to sent to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to sent mail: " + e.getMessage());
        }
    }
}