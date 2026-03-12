package com.phamtra.identity_service.repository;

import com.phamtra.identity_service.model.RefreshToken;
import com.phamtra.identity_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserIdAndRevokedFalse(Long userId);

    List<RefreshToken> findByUser(User user);

    void deleteByUser(User user);
}
