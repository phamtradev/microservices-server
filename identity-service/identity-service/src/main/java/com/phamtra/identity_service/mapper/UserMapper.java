package com.phamtra.identity_service.mapper;

import com.phamtra.identity_service.dto.request.RegisterRequest;
import com.phamtra.identity_service.dto.response.RegisterResponse;
import com.phamtra.identity_service.dto.response.UserResponse;
import com.phamtra.identity_service.model.Role;
import com.phamtra.identity_service.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toUser(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    public RegisterResponse toRegisterResponse(User user) {
        if (user == null) {
            return null;
        }
        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.isActive())
                .roles(mapRolesToRoleResponses(user.getRoles()))
                .build();
    }

    public Set<UserResponse.RoleResponse> mapRolesToRoleResponses(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toSet());
    }

    public UserResponse.RoleResponse toRoleResponse(Role role) {
        if (role == null) {
            return null;
        }
        return UserResponse.RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
