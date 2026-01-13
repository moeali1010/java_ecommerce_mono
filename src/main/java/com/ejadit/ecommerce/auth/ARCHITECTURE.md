auth/
├── domain/                           # Domain Layer - Pure Business Logic
│   ├── entity/
│   │   ├── AuthenticationToken.java   # Entity with identity and behavior
│   │   ├── PasswordReset.java         # Entity with identity and behavior
│   │   ├── TokenType.java             # Enum for token types
│   │   └── ResetStatus.java           # Enum for reset status
│   └── valueobject/
│       ├── Email.java                 # ValueObject - enforces email rules
│       ├── Password.java              # ValueObject - enforces password rules
│       └── Username.java              # ValueObject - enforces username rules
│
├── repository/                       # Infrastructure Layer - Data Access
│   ├── AuthenticationTokenRepository.java  # JPA Repository interface
│   └── PasswordResetRepository.java        # JPA Repository interface
│
├── service/                          # Application Layer - Use Cases
│   ├── IAuthService.java            # Service interface
│   └── impl/
│       └── AuthServiceImpl.java       # Service implementation with DDD logic
│
├── mapper/                           # Application Layer - DTO Mapping
│   └── AuthMapper.java              # MapStruct mapper
│
├── controller/                       # Interface Layer - HTTP REST
│   └── AuthController.java          # REST endpoints
│
├── dto/                              # Application Layer - Data Transfer Objects
│   ├── RegisterRequestDto.java       # Registration request
│   ├── LoginRequestDto.java          # Login request
│   ├── LoginResponseDto.java         # Login response with tokens
│   ├── ForgotPasswordRequestDto.java # Forgot password request
│   ├── ResetPasswordRequestDto.java  # Reset password request
│   └── AuthResponseDto.java          # General auth response
│
├── exception/                        # Application Layer - Custom Exceptions
│   ├── AuthenticationException.java  # Auth-specific exception
│   └── TokenException.java           # Token-specific exception
│
└── README.md                         # Module documentation


═══════════════════════════════════════════════════════════════════════════════
                    DOMAIN-DRIVEN DESIGN ARCHITECTURE
═══════════════════════════════════════════════════════════════════════════════

                          ┌─────────────────────┐
                          │  INTERFACE LAYER    │
                          │  (REST Controllers) │
                          │  /api/v1/auth/**    │
                          └──────────┬──────────┘
                                     │
                          ┌──────────▼──────────┐
                          │ APPLICATION LAYER   │
                          │ (Services & DTOs)   │
                          │ IAuthService        │
                          │ AuthServiceImpl      │
                          │ AuthMapper          │
                          └──────────┬──────────┘
                                     │
                          ┌──────────▼──────────┐
                          │   DOMAIN LAYER      │
                          │ (Business Rules)    │
                          │                     │
                          │ Entities:           │
                          │ - AuthenticationToken
                          │ - PasswordReset     │
                          │                     │
                          │ ValueObjects:       │
                          │ - Email             │
                          │ - Username          │
                          │ - Password          │
                          │                     │
                          │ Enums:              │
                          │ - TokenType         │
                          │ - ResetStatus       │
                          └──────────┬──────────┘
                                     │
                          ┌──────────▼──────────┐
                          │INFRASTRUCTURE LAYER │
                          │ (Repositories)      │
                          │ Database            │
                          │ (Flyway migrations) │
                          └─────────────────────┘


═══════════════════════════════════════════════════════════════════════════════
                         USER REGISTRATION FLOW (DDD)
═══════════════════════════════════════════════════════════════════════════════

1. HTTP Request
   ↓
2. AuthController.register()
   ↓
3. AuthServiceImpl.register()
   ├─ Username.create() ─→ Validate username rules
   ├─ Email.create()    ─→ Validate email format
   ├─ Password.create() ─→ Validate password strength
   ├─ Check duplicates
   ├─ UserEntity.create() ─→ Factory method creates aggregate
   ├─ Save to UserRepository
   └─ Return AuthResponseDto


═══════════════════════════════════════════════════════════════════════════════
                           USER LOGIN FLOW (DDD)
═══════════════════════════════════════════════════════════════════════════════

1. HTTP Request
   ↓
2. AuthController.login()
   ↓
3. AuthServiceImpl.login()
   ├─ Find user by username
   ├─ Verify password (PasswordEncoder)
   ├─ Generate tokens (UUID)
   ├─ AuthenticationToken.create() ─→ Creates TOKEN aggregate
   ├─ AuthenticationToken.create() ─→ Creates REFRESH_TOKEN aggregate
   ├─ Save tokens to repository
   └─ Return LoginResponseDto with tokens


═══════════════════════════════════════════════════════════════════════════════
                      PASSWORD RESET FLOW (DDD)
═══════════════════════════════════════════════════════════════════════════════

FORGOT PASSWORD:
1. ForgotPasswordRequestDto
   ↓
2. AuthServiceImpl.forgotPassword()
   ├─ Find user by email
   ├─ Revoke previous reset tokens
   ├─ PasswordReset.create() ─→ Creates RESET aggregate with PENDING status
   ├─ Save to repository
   └─ Send email with token (TODO)

RESET PASSWORD:
1. ResetPasswordRequestDto with token
   ↓
2. AuthServiceImpl.resetPassword()
   ├─ Find valid PasswordReset by token
   ├─ Validate passwords match
   ├─ reset.use() ─→ Updates status to USED
   ├─ Encode new password
   ├─ Update user entity
   ├─ Save changes
   └─ Return success response


═══════════════════════════════════════════════════════════════════════════════
                      DATABASE SCHEMA (NORMALIZED)
═══════════════════════════════════════════════════════════════════════════════

                              ┌─────────────┐
                              │   users     │
                              ├─────────────┤
                              │ user_id (PK)│
                              │ name        │
                              │ user_name   │
                              │ password    │
                              │ email       │
                              │ user_type   │
                              │ user_status │
                              └──────┬──────┘
                                     │
                   ┌─────────────────┼─────────────────┐
                   │                 │                 │
        ┌──────────▼────────────┐    │    ┌───────────▼──────────────┐
        │authentication_tokens  │    │    │ password_resets          │
        ├───────────────────────┤    │    ├──────────────────────────┤
        │ token_id (PK)         │    │    │ reset_id (PK)            │
        │ user_id (FK) ◄────────┼────┘    │ user_id (FK) ◄───────────┼──┐
        │ token (UQ)            │         │ email                    │  │
        │ token_type            │         │ token (UQ)               │  │
        │ expires_at            │         │ status                   │  │
        │ is_revoked            │         │ created_at               │  │
        │ created_at            │         │ expires_at               │  │
        └───────────────────────┘         │ used_at (nullable)       │  │
                                          └──────────────────────────┘  │
                                                           Referenced ───┘

FOREIGN KEY RELATIONSHIPS:
- authentication_tokens.user_id → users.user_id (CASCADE DELETE)
- password_resets.user_id → users.user_id (CASCADE DELETE)

INDEXES:
- authentication_tokens: token, user_id, expires_at, (user_id, token_type)
- password_resets: token, user_id, email, expires_at, status


═══════════════════════════════════════════════════════════════════════════════
                        DDD KEY DESIGN DECISIONS
═══════════════════════════════════════════════════════════════════════════════

1. VALUE OBJECTS (Email, Username, Password)
   ✓ Enforce domain rules at creation time
   ✓ Immutable and comparable by value
   ✓ Prevent invalid domain states
   
2. ENTITIES WITH BEHAVIOR
   ✓ AuthenticationToken.isValid(), isExpired(), revoke()
   ✓ PasswordReset.use(), revoke(), isValid()
   ✓ Encapsulate business rules in domain objects
   
3. FACTORY METHODS
   ✓ AuthenticationToken.create() - Creates valid tokens
   ✓ PasswordReset.create() - Creates valid reset requests
   ✓ Ensure only valid aggregates are created
   
4. REPOSITORY PATTERN
   ✓ AuthenticationTokenRepository - Access tokens
   ✓ PasswordResetRepository - Reset requests
   ✓ Aggregate root is UserEntity (existing)
   
5. SERVICE ORCHESTRATION
   ✓ AuthServiceImpl orchestrates domain objects
   ✓ Translates use cases to domain operations
   ✓ Handles cross-aggregate concerns
   
6. SEPARATION OF CONCERNS
   ✓ Domain layer: Pure business logic (no Spring dependencies)
   ✓ Application layer: Use cases and DTO mapping
   ✓ Interface layer: HTTP/REST concerns
   ✓ Infrastructure layer: Database and externals

═══════════════════════════════════════════════════════════════════════════════
