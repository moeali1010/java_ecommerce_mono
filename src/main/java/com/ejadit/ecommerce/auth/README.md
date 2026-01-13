# Authentication Module - Domain Driven Design

## Overview
This module implements a comprehensive authentication system following **Domain-Driven Design (DDD)** principles. It includes user registration, login, password reset, and token management functionality.

## Architecture

### Domain Layer

#### ValueObjects
Domain value objects enforce business rules and constraints:

1. **Email** - `valueobject/Email.java`
   - Validates email format
   - Auto-normalizes to lowercase
   - Immutable and comparable by value

2. **Username** - `valueobject/Username.java`
   - Min length: 3, Max length: 50
   - Only alphanumeric, underscore, and hyphen characters
   - Auto-normalizes to lowercase
   - Immutable and comparable by value

3. **Password** - `valueobject/Password.java`
   - Min length: 6 characters
   - Stored as hashed value
   - Includes password matching logic
   - Never returns plain password (toString returns "****")

#### Entities
Domain entities represent core business concepts:

1. **AuthenticationToken** - `domain/entity/AuthenticationToken.java`
   - Represents JWT/authentication tokens
   - Types: ACCESS_TOKEN, REFRESH_TOKEN, PASSWORD_RESET_TOKEN, EMAIL_VERIFICATION_TOKEN
   - Tracks expiration and revocation status
   - Factory method: `AuthenticationToken.create()`
   - Behavior methods: `isValid()`, `isExpired()`, `revoke()`

2. **PasswordReset** - `domain/entity/PasswordReset.java`
   - Represents password reset requests
   - Status: PENDING, USED, REVOKED, EXPIRED
   - Tracks creation, expiration, and usage
   - Factory method: `PasswordReset.create()`
   - Behavior methods: `isValid()`, `isExpired()`, `use()`, `revoke()`

#### Enums
- **TokenType** - Types of authentication tokens
- **ResetStatus** - Status of password reset requests

### Application Layer

#### DTOs (Data Transfer Objects)

**Request DTOs:**
- `RegisterRequestDto` - User registration request
- `LoginRequestDto` - User login request
- `ForgotPasswordRequestDto` - Password reset request
- `ResetPasswordRequestDto` - Password reset completion

**Response DTOs:**
- `LoginResponseDto` - Login response with tokens
- `AuthResponseDto` - General authentication response

#### Service Layer

**IAuthService** - `service/IAuthService.java`
Public interface defining authentication operations:
- `register()` - Register new user
- `login()` - Authenticate user
- `forgotPassword()` - Request password reset
- `resetPassword()` - Complete password reset
- `validateToken()` - Validate authentication token
- `logout()` - Revoke token
- `refreshToken()` - Generate new access token

**AuthServiceImpl** - `service/impl/AuthServiceImpl.java`
Implementation with business logic:

```java
// Constants
- ACCESS_TOKEN_EXPIRATION_MINUTES = 60
- REFRESH_TOKEN_EXPIRATION_DAYS = 7
- PASSWORD_RESET_EXPIRATION_MINUTES = 30

// Key methods
- register(): Create new user with validation
- login(): Authenticate and generate tokens
- forgotPassword(): Create password reset token
- resetPassword(): Update password using valid token
- validateToken(): Check token validity and expiration
- logout(): Revoke authentication token
- refreshToken(): Generate new access token from refresh token
```

#### Mapper

**AuthMapper** - `mapper/AuthMapper.java`
MapStruct mapper for DTO conversions:
- Maps UserEntity to AuthResponseDto
- Maps UserEntity + tokens to LoginResponseDto

### Interface Layer

**AuthController** - `controller/AuthController.java`
REST endpoints (base path: `/api/v1/auth`):

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register new user |
| POST | `/login` | User login |
| POST | `/forgot-password` | Request password reset |
| POST | `/reset-password` | Reset password with token |
| GET | `/validate-token` | Validate authentication token |
| POST | `/logout` | Logout user |
| POST | `/refresh-token` | Refresh access token |

All endpoints include:
- OpenAPI/Swagger documentation
- Input validation
- Error handling
- Logging

### Infrastructure Layer

#### Repositories

**AuthenticationTokenRepository** - `repository/AuthenticationTokenRepository.java`
- Find tokens by various criteria
- Query valid non-revoked tokens
- Delete expired tokens

**PasswordResetRepository** - `repository/PasswordResetRepository.java`
- Find reset tokens by token string
- Query valid pending resets
- Find latest valid reset for user
- Delete expired resets

#### Database Schema

**V6__Create_AuthenticationTokens_Table.sql**
```sql
CREATE TABLE authentication_tokens (
    token_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    token_type ENUM(...),
    expires_at DATETIME NOT NULL,
    is_revoked TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_token,
    INDEX idx_user_id,
    INDEX idx_expires_at,
    INDEX idx_user_token_type
)
```

**V7__Create_PasswordResets_Table.sql**
```sql
CREATE TABLE password_resets (
    reset_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    status ENUM('PENDING', 'USED', 'REVOKED', 'EXPIRED'),
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_token,
    INDEX idx_user_id,
    INDEX idx_email,
    INDEX idx_expires_at,
    INDEX idx_status
)
```

### Exception Handling

Custom exceptions in `exception/` package:
- **AuthenticationException** - General authentication failures
- **TokenException** - Token-related issues

Both extend `BusinessException` for consistent error handling.

## Domain-Driven Design Principles Applied

### 1. Ubiquitous Language
- Register, Login, ForgotPassword, ResetPassword
- AuthenticationToken, PasswordReset, TokenType, ResetStatus
- ValueObjects: Email, Username, Password

### 2. Entities & Value Objects
- ValueObjects enforce business rules (Email format, Username constraints, Password strength)
- Entities with identity (AuthenticationToken, PasswordReset) track state and behavior
- Factory methods create valid instances: `AuthenticationToken.create()`, `PasswordReset.create()`

### 3. Aggregates
- User aggregate (UserEntity) + AuthenticationToken relationship
- PasswordReset aggregate for password reset flows
- Repositories as aggregate roots

### 4. Domain Services
- AuthServiceImpl orchestrates domain logic
- Uses repositories to persist aggregates
- Leverages value objects and entity methods

### 5. Separation of Concerns
- **Domain Layer**: Pure business rules (ValueObjects, Entities, Repositories interfaces)
- **Application Layer**: Use cases and orchestration (Service implementations)
- **Interface Layer**: HTTP concerns (Controller)
- **Infrastructure Layer**: Database and external systems (Repository implementations)

## Key Features

### 1. User Registration
- Validates username, email, password through ValueObjects
- Checks for existing users
- Encodes password with PasswordEncoder
- Creates user with CUSTOMER type and ACTIVE status

### 2. User Login
- Validates user exists and is active
- Verifies password using PasswordEncoder
- Generates ACCESS_TOKEN and REFRESH_TOKEN
- Stores tokens in database with expiration

### 3. Password Reset Flow
1. **Forgot Password**: Generate reset token, send via email
2. **Reset Password**: Validate token, update user password, mark token as used

### 4. Token Management
- Token validation with expiration check
- Token revocation on logout
- Refresh token generation for new access tokens
- Automatic cleanup of expired tokens

## Internationalization (i18n)

Messages supported in English and Arabic:
- `messages_en.properties` - English messages
- `messages_ar.properties` - Arabic messages

Key message keys:
```properties
auth.register.success
auth.login.success
auth.password.reset.success
auth.user.inactive
auth.reset.token.invalid
```

## Security Considerations

1. **Password Security**
   - Passwords encoded using Spring's PasswordEncoder
   - Never stored in plain text
   - ValueObject ensures minimum length

2. **Token Security**
   - Tokens stored in database with unique constraint
   - Tokens expire automatically
   - Tokens can be revoked immediately on logout

3. **Domain Validation**
   - Email format validation
   - Username constraints (length, characters)
   - Password strength requirements

## API Usage Examples

### Register
```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Ahmed Ali",
  "username": "ahmed_ali",
  "email": "ahmed@example.com",
  "password": "SecurePassword123",
  "confirmPassword": "SecurePassword123",
  "mobileNumber": "+201000000000"
}
```

### Login
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "ahmed_ali",
  "password": "SecurePassword123"
}
```

### Forgot Password
```bash
POST /api/v1/auth/forgot-password
Content-Type: application/json

{
  "email": "ahmed@example.com"
}
```

### Reset Password
```bash
POST /api/v1/auth/reset-password
Content-Type: application/json

{
  "token": "reset-token-from-email",
  "newPassword": "NewPassword123",
  "confirmPassword": "NewPassword123"
}
```

### Validate Token
```bash
GET /api/v1/auth/validate-token
Authorization: Bearer access-token
```

### Logout
```bash
POST /api/v1/auth/logout
Authorization: Bearer access-token
```

### Refresh Token
```bash
POST /api/v1/auth/refresh-token
Authorization: Bearer refresh-token
```

## Dependencies

- Spring Boot 4.0.1
- Spring Security (crypto)
- Spring Data JPA
- MapStruct (for DTOs)
- Lombok
- MySQL Connector
- Swagger/OpenAPI

## TODO / Future Enhancements

- [ ] Email service integration for password reset links
- [ ] Email verification on registration
- [ ] JWT token implementation (replace UUID)
- [ ] Two-factor authentication (2FA)
- [ ] Social login (Google, Facebook, etc.)
- [ ] Account lockout after failed login attempts
- [ ] Role-based access control (RBAC)
- [ ] Audit logging for authentication events
- [ ] Token blacklist mechanism
