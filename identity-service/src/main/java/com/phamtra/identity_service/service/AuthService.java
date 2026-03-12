package com.phamtra.identity_service.service;

import com.phamtra.identity_service.dto.request.AuthenticationRequest;
import com.phamtra.identity_service.dto.request.ChangePasswordRequest;
import com.phamtra.identity_service.dto.request.RefreshTokenRequest;
import com.phamtra.identity_service.dto.request.RegisterRequest;
import com.phamtra.identity_service.dto.response.AuthenticationResponse;
import com.phamtra.identity_service.dto.response.RefreshTokenResponse;
import com.phamtra.identity_service.dto.response.RegisterResponse;
import com.phamtra.identity_service.dto.response.UserInfoResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);

    void changePassword(ChangePasswordRequest request);

    UserInfoResponse getCurrentUser();
}
