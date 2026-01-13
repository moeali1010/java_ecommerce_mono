# 📋 Logging Configuration Guide

## Overview
تم إعداد نظام logging شامل مع دعم بيئات مختلفة (Local, Staging, Production)

---

## 📁 Files Created

### 1. **Logback Configuration Files**
- `logback-spring.xml` - للـ Local/Development
- `logback-spring-staging.xml` - للـ Staging environment  
- `logback-spring-production.xml` - للـ Production environment

### 2. **Java Classes**
- `LoggerUtil.java` - Utility class للـ logging الموحد
- `LoggingAspect.java` - AOP Aspect للـ automatic logging

### 3. **Dependencies**
- أضفنا `spring-boot-starter-aop` في pom.xml

---

## 🎯 Environment-wise Configuration

### **Local Development**
```bash
mvn spring-boot:run
```
**خصائص:**
- Log Level: DEBUG
- Output: Console + File (logs/app.log)
- Show SQL: ✓ نعم
- Performance: مراقبة الأداء
- Swagger: مفعّل

### **Staging**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=staging"
```
**خصائص:**
- Log Level: INFO
- Output: Console + File (logs/staging.log)
- Show SQL: مخفي
- File Size: 10MB (حد أقصى)
- Retention: 7 أيام
- Swagger: مفعّل

### **Production**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=production"
```
**خصائص:**
- Log Level: WARN
- Output: File فقط (async للأداء)
- Error Logs: منفصلة في error.log
- File Size: 20MB (حد أقصى)
- Retention: 30 يوم
- Total Storage: 1GB حد أقصى
- Swagger: معطّل
- Async Processing: ✓ نعم

---

## 📊 Log Levels

| Level | Local | Staging | Production |
|-------|-------|---------|------------|
| TRACE | ✓ | ✗ | ✗ |
| DEBUG | ✓ | ✓ | ✗ |
| INFO | ✓ | ✓ | ✓ |
| WARN | ✓ | ✓ | ✓ |
| ERROR | ✓ | ✓ | ✓ |

---

## 🚀 How to Use LoggerUtil

### Success Logging
```java
import com.ejadit.ecommerce.common.util.LoggerUtil;

LoggerUtil.logSuccess("User created with ID: %d", userId);
// Output: ✓ SUCCESS: User created with ID: 123
```

### Error Logging
```java
LoggerUtil.logError("Failed to create user", exception);
// Output: ✗ ERROR: Failed to create user
```

### API Logging
```java
LoggerUtil.logApiRequest("POST", "/api/users", userPayload);
LoggerUtil.logApiResponse("/api/users", 201, savedUser);
```

### Database Operations
```java
LoggerUtil.logDatabaseOperation("INSERT", "users", userId);
LoggerUtil.logDatabaseOperation("UPDATE", "orders", orderId);
```

### Security Events
```java
LoggerUtil.logAuthentication("user@example.com", true);
LoggerUtil.logSecurityEvent("Unauthorized access attempt", "admin");
```

### Performance Metrics
```java
long startTime = System.currentTimeMillis();
// ... some operation
long duration = System.currentTimeMillis() - startTime;
LoggerUtil.logPerformance("User creation", duration);
// Output: Slow operation warning if > 1000ms
```

---

## 🔍 Automatic AOP Logging

### Controllers
جميع methods في `*Controller` classes يتم عمل logging لها:
```
→ Controller Method Start: UserController.createUser
← Controller Method End: UserController.createUser (150ms)
```

### Services
جميع methods في `*Service` classes:
```
→ Service Method Start: UserService.findById
← Service Method End: UserService.findById (50ms)
```
إذا استغرقت أكثر من 1000ms:
```
⚠ Slow Service Method: OrderService.processOrder took 2500ms
```

### Repositories
جميع methods في `*Repository`:
```
→ DB Query: UserRepository.findByEmail
← DB Query End: UserRepository.findByEmail (10ms)
```

---

## 📂 Log File Locations

### Local
- `logs/app.log` - جميع السجلات

### Staging
- `logs/staging.log` - جميع السجلات
- تُُقسم حسب التاريخ: `staging-2026-01-13.1.log`

### Production
- `/var/log/ecommerce/app.log` - سجلات عامة
- `/var/log/ecommerce/error.log` - أخطاء فقط
- تُقسم حسب الحجم والتاريخ

---

## ⚙️ Configuration Details

### Local (application.yml)
```yaml
logging:
  level:
    org.springframework: INFO
    com.ejadit: DEBUG
  file:
    name: logs/app.log
```

### Staging (application-staging.yml)
```yaml
logging:
  level:
    org.springframework: INFO
    com.ejadit: DEBUG
  file:
    name: logs/ecommerce-staging.log
    max-size: 5MB
    max-history: 7
```

### Production (application-production.yml)
```yaml
logging:
  level:
    org.springframework: WARN
    com.ejadit: INFO
  file:
    name: logs/ecommerce.log
    max-size: 10MB
    max-history: 30
```

---

## 🎨 Log Format

```
2026-01-13 18:53:51.008 [http-nio-8080-exec-1] INFO com.ejadit.ecommerce.UserController - ✓ SUCCESS: User created
```

التنسيق: `TIMESTAMP [THREAD] LEVEL LOGGER - MESSAGE`

---

## 🔐 Security Considerations

✓ **Passwords & Sensitive Data:**
- لا نسجل كلمات مرور أو توكنات
- استخدم `LoggerUtil.logSecurityEvent()` فقط للأحداث الأمنية

✓ **File Permissions (Production):**
```bash
chmod 700 /var/log/ecommerce
chmod 600 /var/log/ecommerce/*.log
```

---

## 📈 Monitoring Tips

### الخطأ بسرعة (Production)
```bash
tail -f /var/log/ecommerce/error.log
```

### معلومات التطبيق (Development)
```bash
tail -f logs/app.log
```

### أداء العمليات (Staging)
```bash
grep "Slow" logs/staging.log
```

---

## 🔧 Troubleshooting

### لا تظهر السجلات
1. تأكد من أن `logback-spring.xml` موجود في `src/main/resources`
2. تحقق من تفعيل AOP في `@EnableAspectJAutoProxy`
3. أعد بناء المشروع: `mvn clean compile`

### أداء بطيء
- تقليل log level في production إلى `WARN`
- استخدام async appenders (مفعّل بالفعل)

### حجم ملفات السجلات كبير جداً
- قلل `maxHistory` في الـ rolling policy
- قلل `totalSizeCap` حسب متطلباتك

---

## 📝 Example: Complete Service with Logging

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserResponse createUser(CreateUserRequest request) {
        LoggerUtil.logBusinessLogic("Creating new user: %s", request.getEmail());
        
        try {
            User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .build();
                
            User savedUser = userRepository.save(user);
            LoggerUtil.logDatabaseOperation("INSERT", "users", savedUser.getId());
            LoggerUtil.logSuccess("User created with ID: %d", savedUser.getId());
            
            return UserResponse.from(savedUser);
        } catch (Exception e) {
            LoggerUtil.logError("Failed to create user", e);
            throw new UserCreationException("Failed to create user", e);
        }
    }
}
```

---

**تم إعداد الـ logging بالكامل! ✓**
