package com.phamtra.identity_service.mapper;

import com.phamtra.identity_service.dto.request.CreateProfileRequest;
import com.phamtra.identity_service.dto.request.CreateUserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    CreateProfileRequest toCreateProfileRequest(CreateUserRequest request);
}
