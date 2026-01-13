package com.ejadit.ecommerce.auth.repository;

import com.ejadit.ecommerce.auth.domain.entity.PasswordReset;
import com.ejadit.ecommerce.auth.domain.entity.ResetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    Optional<PasswordReset> findByToken(String token);

    Optional<PasswordReset> findByUserIdAndStatusAndExpiresAtAfter(Long userId, ResetStatus status, LocalDateTime now);

    @Query("SELECT pr FROM PasswordReset pr WHERE pr.userId = :userId AND pr.status = 'PENDING' AND pr.expiresAt > CURRENT_TIMESTAMP ORDER BY pr.createdAt DESC LIMIT 1")
    Optional<PasswordReset> findLatestValidResetByUserId(@Param("userId") Long userId);

    @Query("SELECT pr FROM PasswordReset pr WHERE pr.token = :token AND pr.status = 'PENDING' AND pr.expiresAt > CURRENT_TIMESTAMP")
    Optional<PasswordReset> findValidResetByToken(@Param("token") String token);

    List<PasswordReset> findByExpiresAtBeforeAndStatus(LocalDateTime expiresAt, ResetStatus status);

    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
