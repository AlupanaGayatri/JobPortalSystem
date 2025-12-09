package com.jobportal.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/**
 * AOP Aspect for automatic logging of controller and service methods
 * Adds correlation IDs for request tracing
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Pointcut for all controller methods
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {
    }

    /**
     * Pointcut for all service methods
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {
    }

    /**
     * Before advice for REST controllers - add correlation ID
     */
    @Before("restControllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        // Generate and set correlation ID for request tracing
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID_KEY, correlationId);

        logger.info("==> REST API Call: {}.{}() with arguments: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * After returning advice for REST controllers
     */
    @AfterReturning(pointcut = "restControllerMethods()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        logger.info("<== REST API Response: {}.{}() returned successfully",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());

        // Clear correlation ID
        MDC.remove(CORRELATION_ID_KEY);
    }

    /**
     * After throwing advice for REST controllers
     */
    @AfterThrowing(pointcut = "restControllerMethods()", throwing = "exception")
    public void logAfterThrowingController(JoinPoint joinPoint, Throwable exception) {
        logger.error("<== REST API Error: {}.{}() threw exception: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getMessage());

        // Clear correlation ID
        MDC.remove(CORRELATION_ID_KEY);
    }

    /**
     * Around advice for service methods - log execution time
     */
    @Around("serviceMethods()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        logger.debug("---> Service Call: {}.{}()",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            logger.debug("<--- Service Return: {}.{}() executed in {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime);

            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("<--- Service Error: {}.{}() threw exception after {} ms: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime,
                    ex.getMessage());
            throw ex;
        }
    }
}
