package com.phamtra.profile_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserProfileResponse {

    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String city;
}
