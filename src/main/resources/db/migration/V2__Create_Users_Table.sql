-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mobile_number VARCHAR(50) NOT NULL UNIQUE,
    user_type VARCHAR(20) NOT NULL,
    user_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by VARCHAR(255),
    created_at DATETIME,
    updated_by VARCHAR(255),
    updated_at DATETIME,
    INDEX idx_user_name (user_name),
    INDEX idx_email (email),
    INDEX idx_mobile_number (mobile_number),
    INDEX idx_user_type (user_type),
    INDEX idx_user_status (user_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
