package com.ejadit.ecommerce.auth.repository;

import com.ejadit.ecommerce.auth.domain.entity.AuthenticationToken;
import com.ejadit.ecommerce.auth.domain.entity.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthenticationTokenRepository extends JpaRepository<AuthenticationToken, Long> {

    Optional<AuthenticationToken> findByToken(String token);

    Optional<AuthenticationToken> findByUserIdAndTokenTypeAndIsRevokedFalse(Long userId, TokenType tokenType);

    List<AuthenticationToken> findByUserIdAndIsRevokedFalse(Long userId);

    List<AuthenticationToken> findByExpiresAtBeforeAndIsRevokedFalse(LocalDateTime expiresAt);

    @Query("SELECT t FROM AuthenticationToken t WHERE t.userId = :userId AND t.tokenType = :tokenType AND t.isRevoked = false AND t.expiresAt > CURRENT_TIMESTAMP")
    Optional<AuthenticationToken> findValidTokenByUserAndType(@Param("userId") Long userId, @Param("tokenType") TokenType tokenType);

    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
