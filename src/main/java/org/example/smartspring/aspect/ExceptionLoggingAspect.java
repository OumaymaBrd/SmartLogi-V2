package org.example.smartspring.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @Pointcut("within(org.example.smartspring..*)")
    public void applicationPackage() {}

    @AfterThrowing(pointcut = "applicationPackage()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.error("✗ Exception in {}.{}: {}",
                className.substring(className.lastIndexOf('.') + 1),
                methodName,
                exception.getMessage(),
                exception);


    }
}
