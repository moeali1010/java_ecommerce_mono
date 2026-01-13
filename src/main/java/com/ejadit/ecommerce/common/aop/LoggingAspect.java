package com.ejadit.ecommerce.common.aop;

import com.ejadit.ecommerce.common.util.LoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect للـ Logging
 * يوفر logging تلقائي لـ controllers والـ services
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Logging لـ API Controllers
     */
    @Around("execution(* com.ejadit.ecommerce..controller.*Controller.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            log.info("→ Controller Method Start: {}.{}", className, methodName);
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("← Controller Method End: {}.{} ({}ms)", className, methodName, duration);
            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("✗ Controller Method Error: {}.{} ({}ms)", className, methodName, duration, ex);
            throw ex;
        }
    }

    /**
     * Logging لـ Services
     */
    @Around("execution(* com.ejadit.ecommerce..service.*Service.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            log.debug("→ Service Method Start: {}.{}", className, methodName);
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            // تحذير إذا كانت العملية بطيئة
            if (duration > 1000) {
                log.warn("⚠ Slow Service Method: {}.{} took {}ms", className, methodName, duration);
            } else {
                log.debug("← Service Method End: {}.{} ({}ms)", className, methodName, duration);
            }
            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("✗ Service Method Error: {}.{} ({}ms)", className, methodName, duration, ex);
            throw ex;
        }
    }

    /**
     * Logging لـ Repositories
     */
    @Around("execution(* com.ejadit.ecommerce..repository.*Repository.*(..))")
    public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            log.debug("→ DB Query: {}.{}", className, methodName);
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.debug("← DB Query End: {}.{} ({}ms)", className, methodName, duration);
            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("✗ DB Query Error: {}.{} ({}ms)", className, methodName, duration, ex);
            throw ex;
        }
    }
}
