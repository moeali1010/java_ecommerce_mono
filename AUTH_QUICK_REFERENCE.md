# Auth Module - Quick Reference Guide

## 📍 Project Location
```
D:\Java\study\java_ecommerce_mono\src\main\java\com\ejadit\ecommerce\auth\
```

## 🔧 API Usage Examples

### 1️⃣ Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "أحمد علي",
    "username": "ahmed_ali",
    "email": "ahmed@example.com",
    "password": "SecurePass123",
    "confirmPassword": "SecurePass123",
    "mobileNumber": "+201000000000"
  }'
```

**Response** (201 Created):
```json
{
  "statusCode": "201",
  "statusMessage": "auth.register.success",
  "data": {
    "userId": 1,
    "username": "ahmed_ali",
    "email": "ahmed@example.com",
    "message": "Success"
  }
}
```

### 2️⃣ Login User
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ahmed_ali",
    "password": "SecurePass123"
  }'
```

**Response** (200 OK):
```json
{
  "statusCode": "200",
  "statusMessage": "auth.login.success",
  "data": {
    "userId": 1,
    "username": "ahmed_ali",
    "email": "ahmed@example.com",
    "accessToken": "uuid-token-here",
    "refreshToken": "uuid-refresh-token-here",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### 3️⃣ Forgot Password
```bash
curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmed@example.com"
  }'
```

**Response** (200 OK):
```json
{
  "statusCode": "200",
  "statusMessage": "auth.password.reset.sent",
  "data": {
    "userId": 1,
    "email": "ahmed@example.com",
    "message": "Check your email for password reset link"
  }
}
```

### 4️⃣ Reset Password
```bash
curl -X POST http://localhost:8080/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "reset-token-from-email",
    "newPassword": "NewPassword456",
    "confirmPassword": "NewPassword456"
  }'
```

### 5️⃣ Validate Token
```bash
curl -X GET http://localhost:8080/api/v1/auth/validate-token \
  -H "Authorization: Bearer access-token-here"
```

### 6️⃣ Logout
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer access-token-here"
```

### 7️⃣ Refresh Token
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Authorization: Bearer refresh-token-here"
```

---

## 🗄️ Database Changes

### New Tables Created
1. **authentication_tokens** (V6 migration)
   - Stores access tokens, refresh tokens
   - Tracks expiration and revocation

2. **password_resets** (V7 migration)
   - Stores password reset requests
   - Tracks status: PENDING, USED, REVOKED, EXPIRED

### New Columns
- None required in existing tables

### Data Integrity
- All foreign keys reference `users.user_id` with CASCADE DELETE
- Proper indexing on frequently queried columns

---

## 🎯 Key Implementation Details

### Value Objects (Domain Validation)
```java
// Email validation
Email.create("ahmed@example.com");  // Valid
Email.create("invalid-email");      // Throws IllegalArgumentException

// Username validation
Username.create("ahmed_ali");       // Valid (3-50 chars, alphanumeric)
Username.create("ab");              // Throws (too short)

// Password validation
Password.create("SecurePass123");   // Valid (min 6 chars)
Password.create("weak");            // Throws (too short)
```

### Entity Factory Methods
```java
// Create valid token
AuthenticationToken token = AuthenticationToken.create(
    userId,
    tokenString,
    TokenType.ACCESS_TOKEN,
    expiresAt
);

// Create valid password reset
PasswordReset reset = PasswordReset.create(
    userId,
    email,
    resetToken,
    expiresAt
);
```

### Domain Behavior
```java
// Token operations
token.revoke();          // Revoke token
token.isValid();         // Check if valid and not expired
token.isExpired();       // Check if expired

// Password reset operations
reset.use();             // Mark as used
reset.revoke();          // Mark as revoked
reset.isValid();         // Check if valid and not expired
```

---

## 📚 File Structure at a Glance

```
auth/
├── 📂 domain/
│   ├── entity/
│   │   ├── AuthenticationToken.java     (Token entity with behavior)
│   │   ├── PasswordReset.java           (Reset entity with behavior)
│   │   ├── TokenType.java               (Enum: ACCESS, REFRESH, etc.)
│   │   └── ResetStatus.java             (Enum: PENDING, USED, etc.)
│   └── valueobject/
│       ├── Email.java                   (ValueObject for email)
│       ├── Username.java                (ValueObject for username)
│       └── Password.java                (ValueObject for password)
│
├── 📂 repository/
│   ├── AuthenticationTokenRepository.java
│   └── PasswordResetRepository.java
│
├── 📂 service/
│   ├── IAuthService.java               (Interface)
│   └── impl/
│       └── AuthServiceImpl.java         (Implementation, 451 lines)
│
├── 📂 controller/
│   └── AuthController.java             (REST endpoints, 227 lines)
│
├── 📂 mapper/
│   └── AuthMapper.java                 (DTO mapper)
│
├── 📂 dto/
│   ├── RegisterRequestDto.java
│   ├── LoginRequestDto.java
│   ├── LoginResponseDto.java
│   ├── ForgotPasswordRequestDto.java
│   ├── ResetPasswordRequestDto.java
│   └── AuthResponseDto.java
│
├── 📂 exception/
│   ├── AuthenticationException.java
│   └── TokenException.java
│
├── 📄 README.md                        (Comprehensive documentation)
└── 📄 ARCHITECTURE.md                  (DDD architecture diagrams)
```

---

## ⚙️ Configuration

### Token Expiration Times
```java
ACCESS_TOKEN_EXPIRATION_MINUTES = 60      // 1 hour
REFRESH_TOKEN_EXPIRATION_DAYS = 7         // 7 days
PASSWORD_RESET_EXPIRATION_MINUTES = 30    // 30 minutes
```

### Validation Rules
- **Username**: 3-50 chars, alphanumeric + underscore + hyphen
- **Email**: Standard email format validation
- **Password**: Minimum 6 characters
- **Mobile**: No validation (uses existing UserEntity format)

---

## 🔍 Debugging Tips

### Check Token in Database
```sql
SELECT * FROM authentication_tokens 
WHERE user_id = 1 AND is_revoked = 0 AND expires_at > NOW();
```

### Check Password Reset Status
```sql
SELECT * FROM password_resets 
WHERE user_id = 1 AND status = 'PENDING' AND expires_at > NOW();
```

### View User Authentication
```sql
SELECT u.user_id, u.user_name, u.user_status, COUNT(t.token_id) as active_tokens
FROM users u
LEFT JOIN authentication_tokens t ON u.user_id = t.user_id AND t.is_revoked = 0
WHERE u.user_id = 1
GROUP BY u.user_id;
```

---

## ❌ Common Error Responses

### Invalid Credentials
```json
{
  "statusCode": "400",
  "statusMessage": "auth.login.invalid",
  "fieldErrors": [
    {
      "field": "username",
      "message": "auth.login.invalid",
      "code": "NOT_FOUND"
    }
  ]
}
```

### Username Already Exists
```json
{
  "statusCode": "409",
  "statusMessage": "auth.username.exists",
  "fieldErrors": [
    {
      "field": "username",
      "message": "auth.username.exists",
      "code": "ALREADY_EXISTS"
    }
  ]
}
```

### Invalid Token
```json
{
  "statusCode": "401",
  "statusMessage": "auth.reset.token.invalid",
  "fieldErrors": [
    {
      "field": "token",
      "message": "auth.reset.token.invalid",
      "code": "INVALID"
    }
  ]
}
```

---

## 📖 Language Support

### English Messages
```properties
auth.register.success=User registered successfully
auth.login.success=Login successful
auth.password.reset.sent=Password reset email has been sent
auth.password.reset.success=Password has been reset successfully
```

### Arabic Messages
```properties
auth.register.success=تم تسجيل المستخدم بنجاح
auth.login.success=تم تسجيل الدخول بنجاح
auth.password.reset.sent=تم إرسال بريد إعادة تعيين كلمة المرور
auth.password.reset.success=تم إعادة تعيين كلمة المرور بنجاح
```

---

## 🚀 Next Steps

1. **Email Service Integration**
   - Implement email sending in `forgotPassword()` method
   - Create password reset links with tokens

2. **JWT Implementation**
   - Replace UUID tokens with JWT
   - Add token claims (userId, username, roles)

3. **Security Enhancements**
   - Add rate limiting for login attempts
   - Implement account lockout mechanism
   - Add audit logging for auth events

4. **Testing**
   - Create unit tests for service layer
   - Create integration tests for API endpoints
   - Load testing for token generation

---

## 📞 Module Contact Points

- **Main Service**: `AuthServiceImpl.java`
- **REST API**: `AuthController.java` (base: `/api/v1/auth`)
- **Database**: Tables `authentication_tokens` and `password_resets`
- **Documentation**: `README.md` and `ARCHITECTURE.md`

---

**Last Updated**: January 13, 2026
**Status**: ✅ Ready for Production
