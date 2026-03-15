package com.phamtra.profile_service.controller;

import com.phamtra.profile_service.service.UserProfileService;
import com.phamtra.profile_service.dto.response.CreateUserProfileResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/users")
    public List<CreateUserProfileResponse> getAllProfiles() {
        return userProfileService.getAllProfiles();
    }

    @GetMapping("/user/{id}")
    public CreateUserProfileResponse getProfileById(@PathVariable String id) {
        return userProfileService.getProfileById(id);
    }

}
