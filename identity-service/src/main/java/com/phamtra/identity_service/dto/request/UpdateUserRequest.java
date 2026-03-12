package com.phamtra.identity_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Email should be valid")
    private String email;

    private String firstName;

    private String lastName;

    private boolean active;

    private String password;

    private Set<String> roleCodes;
}
