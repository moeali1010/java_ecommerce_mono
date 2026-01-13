-- Migration: Create authentication_tokens table
-- Description: Store access and refresh tokens for user authentication

CREATE TABLE IF NOT EXISTS authentication_tokens (
    token_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    token_type ENUM('ACCESS_TOKEN', 'REFRESH_TOKEN', 'PASSWORD_RESET_TOKEN', 'EMAIL_VERIFICATION_TOKEN') NOT NULL,
    expires_at DATETIME NOT NULL,
    is_revoked TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at),
    INDEX idx_user_token_type (user_id, token_type)
);
