package com.phamtra.identity_service.service.Impl;

import com.phamtra.identity_service.dto.response.IntrospectResponse;
import com.phamtra.identity_service.model.Permission;
import com.phamtra.identity_service.model.RefreshToken;
import com.phamtra.identity_service.model.Role;
import com.phamtra.identity_service.model.User;
import com.phamtra.identity_service.repository.RefreshTokenRepository;
import com.phamtra.identity_service.repository.UserRepository;
import com.phamtra.identity_service.service.IntrospectService;
import com.phamtra.identity_service.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntrospectServiceImpl implements IntrospectService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional(readOnly = true)
    public IntrospectResponse introspect(String token) {
        try {
            Claims claims = jwtUtils.verify(token);

            String email = claims.getSubject();
            if (email == null || email.isBlank()) {
                return inactiveResponse();
            }

            Optional<User> userOptional = userRepository.findByEmailWithRolesAndPermissions(email);
            if (userOptional.isEmpty()) {
                return inactiveResponse();
            }

            User user = userOptional.get();

            if (!user.isActive()) {
                return inactiveResponse();
            }

            List<RefreshToken> refreshTokens = refreshTokenRepository.findByUser(user);
            boolean hasValidSession = refreshTokens != null && refreshTokens.stream()
                    .anyMatch(t -> !t.isRevoked()
                            && t.getExpiresAt() != null
                            && t.getExpiresAt().isAfter(Instant.now()));

            if (!hasValidSession) {
                return inactiveResponse();
            }

            Set<String> roleCodes = user.getRoles() == null
                    ? Collections.emptySet()
                    : user.getRoles().stream()
                    .map(Role::getCode)
                    .collect(Collectors.toSet());

            Set<String> permissionCodes = user.getRoles() == null
                    ? Collections.emptySet()
                    : user.getRoles().stream()
                    .filter(role -> role.getPermissions() != null)
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getCode)
                    .collect(Collectors.toSet());

            return IntrospectResponse.builder()
                    .active(true)
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userActive(user.isActive())
                    .roles(roleCodes)
                    .permissions(permissionCodes)
                    .issuedAt(claims.getIssuedAt() != null ? claims.getIssuedAt().getTime() : null)
                    .expiresAt(claims.getExpiration() != null ? claims.getExpiration().getTime() : null)
                    .build();

        } catch (Exception e) {
            return inactiveResponse();
        }
    }

    private IntrospectResponse inactiveResponse() {
        return IntrospectResponse.builder()
                .active(false)
                .build();
    }
}