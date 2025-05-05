package com.sharks.email_service.listeners;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.sharks.email_service.models.EmailDetails;
import com.sharks.email_service.models.EmailDetailsWithAttachment;
import com.sharks.email_service.models.dtos.PdfEmail;
import com.sharks.email_service.models.dtos.UserDTO;
import com.sharks.email_service.services.EmailService;

@Slf4j
@Component
public class EmailListener {

    private EmailService emailService;

    public EmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "user-registration")
    public void sendUserRegistrationEmail(UserDTO user) {
        String subject = "User Registration";
        String message = "Welcome, " + user.getUsername() + "!\n\nYour account has been successfully created.";
        emailService.sendMail(new EmailDetails(user.getEmail(), subject, message));
    }

    @RabbitListener(queues = "accreditation-confirmation")
    public void sendAccreditationConfirmationEmail(PdfEmail pdfEmail) {
        String subject = "Accreditation Confirmation";
        String message = "Hello, " + pdfEmail.getUser().getUsername() +
                "!\n\nYour accreditation has been successfully confirmed.";
        emailService.sendMailWithAttachment(new EmailDetailsWithAttachment(
                pdfEmail.getUser().getEmail(),
                subject,
                message,
                pdfEmail.getPdfBytes(),
                LocalDate.now().toString() + ".pdf",
                "application/pdf"));
    }
}
