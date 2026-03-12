package com.phamtra.identity_service.service;

import com.phamtra.identity_service.dto.request.CreateUserRequest;
import com.phamtra.identity_service.dto.request.SetUserRoleCodesRequest;
import com.phamtra.identity_service.dto.request.UpdateUserRequest;
import com.phamtra.identity_service.dto.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    UserResponse setActive(Long id, boolean active);

    UserResponse setRolesByCodes(Long userId, SetUserRoleCodesRequest request);

    void deleteUser(Long id);
}
