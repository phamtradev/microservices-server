package com.phamtra.profile_service.controller;

import com.phamtra.profile_service.service.UserProfileService;
import com.phamtra.profile_service.dto.request.CreateUserProfileRequest;
import com.phamtra.profile_service.dto.response.CreateUserProfileResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public CreateUserProfileResponse createProfile(@RequestBody CreateUserProfileRequest request) {
        return userProfileService.createUserProfile(request);
    }

    @GetMapping("/{id}")
    public CreateUserProfileResponse getProfileById(@PathVariable String id) {
        return userProfileService.getProfileById(id);
    }

}
