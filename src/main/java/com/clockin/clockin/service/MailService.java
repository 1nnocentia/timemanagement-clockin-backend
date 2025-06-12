package com.clockin.clockin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender; // Injeksi JavaMailSender

    /**
     * Mengirim email sederhana.
     * @param to Alamat email tujuan.
     * @param subject Subjek email.
     * @param text Isi email.
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
            // Dalam aplikasi produksi, Anda mungkin ingin mencatat error ini ke sistem log
            // atau mencoba ulang pengiriman.
            throw new RuntimeException("Failed to sent mail: " + e.getMessage());
        }
    }
}