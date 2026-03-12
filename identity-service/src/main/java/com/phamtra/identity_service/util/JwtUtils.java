package com.phamtra.identity_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.phamtra.identity_service.model.RefreshToken;
import com.phamtra.identity_service.model.User;
import com.phamtra.identity_service.repository.RefreshTokenRepository;
import com.phamtra.identity_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JwtUtils {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${phamtra.jwt.secret}")
    private String secret;

    @Value("${phamtra.jwt.access-token-expiration-seconds:900}")
    private long accessTokenExpirationSeconds;

    public JwtUtils(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Note: cần xóa toàn bộ token cũ của người dùng để tránh trùng lặp
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenExpirationSeconds);

        List<String> roleCodes = user.getRoles() != null ?
                user.getRoles().stream()
                        .map(r -> r.getCode())
                        .collect(Collectors.toList()) : null;

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", roleCodes)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(604800); // 7 days

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(getSigningKey())
                .compact();
    }

    // Note: throw lỗi ra
    // Check tồn tại trong db
    // Check không revoke
    public Claims verify(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Check tồn tại trong db
        String email = claims.getSubject();
        if (email != null) {
            Optional<User> maybeUser = userRepository.findByEmail(email);
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
                if (tokens == null || tokens.isEmpty()) {
                    throw new BadCredentialsException("Session invalid - login again");
                }

                boolean anyActiveNotExpired = tokens.stream().anyMatch(t ->
                        !t.isRevoked() && t.getExpiresAt() != null && t.getExpiresAt().isAfter(Instant.now()));
                if (anyActiveNotExpired) {
                    // OK
                } else {
                    boolean anyActiveExpired = tokens.stream().anyMatch(t ->
                            !t.isRevoked() && t.getExpiresAt() != null && t.getExpiresAt().isBefore(Instant.now()));
                    if (anyActiveExpired) {
                        throw new CredentialsExpiredException("Refresh token expired - login again");
                    } else {
                        // All revoked
                        throw new BadCredentialsException("Session revoked - login again");
                    }
                }
            }
        }

        return claims;
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public long getAccessTokenExpirySeconds() {
        return accessTokenExpirationSeconds;
    }
}
