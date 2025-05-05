package com.sharks.email_service.services.impl;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.sharks.email_service.models.EmailDetails;
import com.sharks.email_service.models.EmailDetailsWithAttachment;
import com.sharks.email_service.services.EmailService;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMail(EmailDetails details) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setTo(details.getRecipient());
        mailMessage.setText(details.getMsgBody());
        mailMessage.setSubject(details.getSubject());

        try {
            javaMailSender.send(mailMessage);
        } catch (MailException e) {
            log.error("Error sending simple email: {}", e.getMessage());
        }
    }

    @Override
    public void sendMailWithAttachment(EmailDetailsWithAttachment details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());

            DataSource dataSource = new ByteArrayDataSource(details.getAttachmentBytes(), details.getAttachmentType());
            mimeMessageHelper.addAttachment(details.getAttachmentName(), dataSource);
        } catch (MessagingException e) {
            log.error("Error creating email with attachment: {}", e.getMessage());
        }

        try {
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            log.error("Error sending email with attachment: {}", e.getMessage());
        }
    }
}
