package com.ejadit.ecommerce.users.entity;

import com.ejadit.ecommerce.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * User entity for managing user accounts.
 * 
 * <p>This entity follows the rich domain model pattern with:
 * <ul>
 *   <li>No public setters - state changes only through domain-specific methods</li>
 *   <li>Factory method for controlled creation</li>
 *   <li>Domain methods with clear business intent (activate, deactivate, changeEmail, etc.)</li>
 *   <li>Encapsulated validation logic</li>
 *   <li>Type-safe user status and role management</li>
 * </ul>
 */
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "mobile_number", nullable = false, unique = true)
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;

    // ================= FACTORY METHOD =================

    public static UserEntity create(String name,
            String userName,
            String password,
            String email,
            String mobileNumber,
            UserType userType,
            UserStatus userStatus) {

        // Domain validation (NOT frontend validation)
        if (userType == null) {
            throw new IllegalArgumentException("validation.userType.required");
        }

        // Default to ACTIVE if not provided
        if (userStatus == null) {
            userStatus = UserStatus.ACTIVE;
        }

        UserEntity user = new UserEntity();
        user.name = name;
        user.userName = userName;
        user.password = password;
        user.email = email;
        user.mobileNumber = mobileNumber;
        user.userType = userType;
        user.userStatus = userStatus;

        return user;
    }

    // ================= DOMAIN-SPECIFIC UPDATE METHODS =================

    /**
     * Updates the user's password (typically after encryption by the service layer).
     * 
     * @param encryptedPassword the encrypted password to set
     */
    public void updatePassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.password.empty");
        }
        this.password = encryptedPassword;
    }

    /**
     * Changes the user's email address.
     * 
     * @param newEmail the new email address
     */
    public void changeEmail(String newEmail) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.email.empty");
        }
        this.email = newEmail;
    }

    /**
     * Changes the user's username.
     * 
     * @param newUserName the new username
     */
    public void changeUserName(String newUserName) {
        if (newUserName == null || newUserName.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.username.empty");
        }
        this.userName = newUserName;
    }

    /**
     * Updates the user's mobile number.
     * 
     * @param newMobileNumber the new mobile number
     */
    public void updateMobileNumber(String newMobileNumber) {
        if (newMobileNumber == null || newMobileNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.mobileNumber.empty");
        }
        this.mobileNumber = newMobileNumber;
    }

    /**
     * Updates the user's full name.
     * 
     * @param newName the new name
     */
    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.name.empty");
        }
        this.name = newName;
    }

    /**
     * Promotes or demotes the user to a different type (e.g., ADMIN to CUSTOMER).
     * 
     * @param newUserType the new user type
     */
    public void changeUserType(UserType newUserType) {
        if (newUserType == null) {
            throw new IllegalArgumentException("validation.userType.null");
        }
        this.userType = newUserType;
    }

    /**
     * Activates the user account.
     */
    public void activate() {
        this.userStatus = UserStatus.ACTIVE;
    }

    /**
     * Deactivates the user account.
     */
    public void deactivate() {
        this.userStatus = UserStatus.INACTIVE;
    }

    /**
     * Changes the user status to a specific status.
     * 
     * @param newStatus the new user status
     */
    public void changeStatus(UserStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("validation.userStatus.null");
        }
        this.userStatus = newStatus;
    }

    /**
     * Checks if the user account is active.
     * 
     * @return true if the user is active, false otherwise
     */
    public boolean isActive() {
        return this.userStatus == UserStatus.ACTIVE;
    }

    /**
     * Checks if the user is an admin.
     * 
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return this.userType == UserType.ADMIN;
    }
}
