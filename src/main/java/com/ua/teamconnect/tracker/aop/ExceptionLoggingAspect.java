package com.ua.teamconnect.tracker.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionLoggingAspect extends AbstractLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(ExceptionLoggingAspect.class);

    @AfterThrowing(
        pointcut = "within(com.ua.teamconnect.tracker.service..*) || within(com.ua.teamconnect.tracker.repository..*)",
        throwing = "exception"
    )
    public void logException(JoinPoint joinPoint, Exception exception) {
        var message = "Exception in %s.%s() with message '%s'".formatted(
            findCallingClassName(joinPoint),
            joinPoint.getSignature().getName(),
            exception.getMessage()
        );
        logger.error(message, exception);
    }
}
