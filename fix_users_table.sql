-- Fix users table columns for user_type and user_status
USE java_ecommerce_mono;

-- Modify user_type column to accommodate longer enum values
ALTER TABLE users MODIFY COLUMN user_type VARCHAR(20) NOT NULL;

-- Modify user_status column (already exists)
ALTER TABLE users MODIFY COLUMN user_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Verify the changes
DESCRIBE users;
