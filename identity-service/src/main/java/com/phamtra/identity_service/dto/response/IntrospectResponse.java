package com.phamtra.identity_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectResponse {
    private boolean active;
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean userActive;
    private Set<String> roles;
    private Set<String> permissions;
    private Long issuedAt;
    private Long expiresAt;
}