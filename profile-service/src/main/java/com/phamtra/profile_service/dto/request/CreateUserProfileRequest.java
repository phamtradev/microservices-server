package com.phamtra.profile_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserProfileRequest {

    private String userId;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String city;
}
