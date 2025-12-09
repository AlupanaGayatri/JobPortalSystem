package com.jobportal.service.impl;

import com.jobportal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired(required = false)
    private JavaMailSender emailSender;

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        if (emailSender == null) {
            logger.warn("JavaMailSender is not configured. Skipping email to: {}", to);
            logger.info("Email Content - Subject: {}, Body: {}", subject, text);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@jobportal.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to: " + to, e);
        }
    }
}
