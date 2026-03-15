package com.phamtra.profile_service.controller.Internal;

import com.phamtra.profile_service.dto.request.CreateUserProfileRequest;
import com.phamtra.profile_service.dto.response.CreateUserProfileResponse;
import com.phamtra.profile_service.service.UserProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class InternalUserProfileController {

    private final UserProfileService userProfileService;

    public InternalUserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/internal/users")
    public CreateUserProfileResponse createProfile(@RequestBody CreateUserProfileRequest request) {
        return userProfileService.createUserProfile(request);
    }
}
