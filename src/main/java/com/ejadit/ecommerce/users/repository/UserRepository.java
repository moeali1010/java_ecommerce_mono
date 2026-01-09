package com.ejadit.ecommerce.users.repository;

import com.ejadit.ecommerce.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByMobileNumber(String mobileNumber);
}
