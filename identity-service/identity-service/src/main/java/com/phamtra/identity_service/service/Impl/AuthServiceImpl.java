package com.phamtra.identity_service.service.Impl;

import com.phamtra.identity_service.dto.request.AuthenticationRequest;
import com.phamtra.identity_service.dto.request.ChangePasswordRequest;
import com.phamtra.identity_service.dto.request.RefreshTokenRequest;
import com.phamtra.identity_service.dto.request.RegisterRequest;
import com.phamtra.identity_service.dto.response.AuthenticationResponse;
import com.phamtra.identity_service.dto.response.RefreshTokenResponse;
import com.phamtra.identity_service.dto.response.RegisterResponse;
import com.phamtra.identity_service.dto.response.UserInfoResponse;
import com.phamtra.identity_service.exception.IdInvalidException;
import com.phamtra.identity_service.mapper.UserMapper;
import com.phamtra.identity_service.model.Permission;
import com.phamtra.identity_service.model.RefreshToken;
import com.phamtra.identity_service.model.Role;
import com.phamtra.identity_service.model.User;
import com.phamtra.identity_service.repository.RefreshTokenRepository;
import com.phamtra.identity_service.repository.RoleRepository;
import com.phamtra.identity_service.repository.UserRepository;
import com.phamtra.identity_service.service.AuthService;
import com.phamtra.identity_service.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        validateEmailNotExists(request.getEmail());
        validatePasswordMatch(request);

        User user = createUserFromRequest(request);
        User savedUser = userRepository.save(user);

        return userMapper.toRegisterResponse(savedUser);
    }

    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IdInvalidException("Email already exists: " + email);
        }
    }

    private void validatePasswordMatch(RegisterRequest request) {
        if (request.getPassword() == null
                || request.getConfirmPassword() == null
                || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new IdInvalidException("Password and confirm password do not match");
        }
    }

    private User createUserFromRequest(RegisterRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRoles(getDefaultUserRole());
        return user;
    }

    private Set<Role> getDefaultUserRole() {
        Role userRole = roleRepository.findByCode("USER")
                .orElseThrow(() -> new IdInvalidException("Default role USER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        return roles;
    }

    @Override
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request) {
        User user = findAndValidateUser(request.getEmail(), request.getPassword());

        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshTokenStr = createRefreshToken(user);

        return buildAuthenticationResponse(accessToken, refreshTokenStr);
    }

    private User findAndValidateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IdInvalidException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IdInvalidException("Invalid credentials");
        }

        return user;
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId())
                .ifPresent(t -> {
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                });

        String refreshTokenStr = UUID.randomUUID().toString();
        RefreshToken refreshToken = buildRefreshToken(user, refreshTokenStr);
        refreshTokenRepository.save(refreshToken);

        return refreshTokenStr;
    }

    private RefreshToken buildRefreshToken(User user, String token) {
        return RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(60L * 60L * 24L * 30L)) // 30 days
                .revoked(false)
                .createdAt(Instant.now())
                .build();
    }

    private AuthenticationResponse buildAuthenticationResponse(
            String accessToken, String refreshToken) {
        return AuthenticationResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtils.getAccessTokenExpirySeconds())
                .build();
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = validateRefreshToken(request.getRefreshToken());
        User user = storedToken.getUser();

        revokeRefreshToken(storedToken);
        String accessToken = jwtUtils.generateAccessToken(user);
        String newRefreshTokenStr = createNewRefreshToken(user);

        return buildRefreshTokenResponse(accessToken, newRefreshTokenStr);
    }

    private RefreshToken validateRefreshToken(String token) {
        RefreshToken stored = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IdInvalidException("Invalid refresh token"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new IdInvalidException("Refresh token expired or revoked");
        }

        return stored;
    }

    private void revokeRefreshToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    private String createNewRefreshToken(User user) {
        String newRefreshTokenStr = UUID.randomUUID().toString();
        RefreshToken newRefreshToken = buildRefreshToken(user, newRefreshTokenStr);
        refreshTokenRepository.save(newRefreshToken);
        return newRefreshTokenStr;
    }

    private RefreshTokenResponse buildRefreshTokenResponse(String accessToken, String refreshToken) {
        return RefreshTokenResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtils.getAccessTokenExpirySeconds())
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        Optional<RefreshToken> stored = refreshTokenRepository.findByToken(refreshToken);
        stored.ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentAuthenticatedUser();
        validateOldPassword(user, request.getOldPassword());
        validateNewPassword(request.getNewPassword());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        if (email == null) {
            throw new IdInvalidException("Not authenticated");
        }
        return userRepository.findByEmailWithRolesAndPermissions(email)
                .orElseThrow(() -> new IdInvalidException("User not found: " + email));
    }

    private void validateOldPassword(User user, String oldPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IdInvalidException("Old password is incorrect");
        }
    }

    private void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IdInvalidException("New password must not be empty");
        }
    }

    @Override
    public UserInfoResponse getCurrentUser() {
        User user = getCurrentAuthenticatedUser();
        
        Set<String> roleCodes = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
        
        Set<String> permissionCodes = user.getRoles().stream()
                .filter(role -> role.getPermissions() != null)
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        
        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.isActive())
                .roles(roleCodes)
                .permissions(permissionCodes)
                .build();
    }
}
