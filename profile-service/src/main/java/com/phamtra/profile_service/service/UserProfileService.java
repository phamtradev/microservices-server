package com.phamtra.profile_service.service;

import com.phamtra.profile_service.dto.request.CreateUserProfileRequest;
import com.phamtra.profile_service.dto.response.CreateUserProfileResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserProfileService {

    CreateUserProfileResponse createUserProfile(CreateUserProfileRequest request);

    CreateUserProfileResponse getProfileById(String id);

    List<CreateUserProfileResponse> getAllProfiles();
}
