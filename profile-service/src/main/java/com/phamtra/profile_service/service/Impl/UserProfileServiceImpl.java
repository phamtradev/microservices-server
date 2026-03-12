package com.phamtra.profile_service.service.Impl;

import com.phamtra.profile_service.service.UserProfileService;
import com.phamtra.profile_service.dto.request.CreateUserProfileRequest;
import com.phamtra.profile_service.dto.response.CreateUserProfileResponse;
import com.phamtra.profile_service.exception.IdInvalidException;
import com.phamtra.profile_service.mapper.UserProfileMapper;
import com.phamtra.profile_service.model.UserProfile;
import com.phamtra.profile_service.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public CreateUserProfileResponse createUserProfile(CreateUserProfileRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        userProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toCreateUserProfileResponse(userProfile);
    }

    @Override
    public CreateUserProfileResponse getProfileById(String id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Profile not found with id: " + id));
        return userProfileMapper.toUserProfileResponse(userProfile);
    }
}
