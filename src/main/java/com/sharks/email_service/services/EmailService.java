package com.sharks.email_service.services;

import com.sharks.email_service.models.EmailDetails;
import com.sharks.email_service.models.EmailDetailsWithAttachment;

public interface EmailService {

    void sendMail(EmailDetails details);

    void sendMailWithAttachment(EmailDetailsWithAttachment details);
}
