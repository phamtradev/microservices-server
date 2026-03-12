package com.phamtra.identity_service.controller;

import com.phamtra.identity_service.dto.request.AuthenticationRequest;
import com.phamtra.identity_service.dto.request.ChangePasswordRequest;
import com.phamtra.identity_service.dto.request.RefreshTokenRequest;
import com.phamtra.identity_service.dto.request.RegisterRequest;
import com.phamtra.identity_service.dto.response.AuthenticationResponse;
import com.phamtra.identity_service.dto.response.RefreshTokenResponse;
import com.phamtra.identity_service.dto.response.RegisterResponse;
import com.phamtra.identity_service.dto.response.UserInfoResponse;
import com.phamtra.identity_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
        UserInfoResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }

    // @GetMapping("/verify-email")
    // public ResponseEntity<Void> verifyEmail(
    //         @RequestParam Long userId,
    //         @RequestParam String token) {
    //     authService.verifyEmailTokenForUser(userId, token);
    //     return ResponseEntity.ok().build();
    // }
}
