package com.phamtra.identity_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetUserRoleCodesRequest {

    @NotNull(message = "Role codes are required")
    private Set<String> roleCodes;
}
