package com.phamtra.profile_service.mapper;

import com.phamtra.profile_service.dto.request.CreateUserProfileRequest;
import com.phamtra.profile_service.dto.response.CreateUserProfileResponse;
import com.phamtra.profile_service.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProfileMapper {

    UserProfile toUserProfile(CreateUserProfileRequest request);

    CreateUserProfileResponse toCreateUserProfileResponse(UserProfile userProfile);

    CreateUserProfileResponse toUserProfileResponse(UserProfile userProfile);
}
