package com.sharks.email_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class UserDTO {

    @NonNull
    private Long id;

    @NonNull
    private String username, email, role;
}
