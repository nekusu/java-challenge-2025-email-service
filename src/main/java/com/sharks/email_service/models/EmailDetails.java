package com.sharks.email_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmailDetails {

    private String recipient, subject, msgBody;
}
