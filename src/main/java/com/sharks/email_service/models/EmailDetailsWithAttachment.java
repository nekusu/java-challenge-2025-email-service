package com.sharks.email_service.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDetailsWithAttachment extends EmailDetails {

    private byte[] attachmentBytes;
    private String attachmentName;
    private String attachmentType;

    public EmailDetailsWithAttachment(
            String recipient,
            String subject,
            String msgBody,
            byte[] attachmentBytes,
            String attachmentName,
            String attachmentType) {
        super(recipient, subject, msgBody);
        this.attachmentBytes = attachmentBytes;
        this.attachmentName = attachmentName;
        this.attachmentType = attachmentType;
    }
}
