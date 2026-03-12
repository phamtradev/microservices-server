package com.phamtra.profile_service.Service;

import com.phamtra.profile_service.dto.request.CreateUserProfileRequest;
import com.phamtra.profile_service.dto.response.CreateUserProfileResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserProfileService {

    CreateUserProfileResponse createUserProfile(CreateUserProfileRequest request);

    CreateUserProfileResponse getProfileById(String id);
}
