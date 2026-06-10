package com.ua.teamconnect.tracker.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ModificationLoggingAspect extends AbstractLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(ModificationLoggingAspect.class);

    @AfterReturning(pointcut = """
    execution(* com.ua.teamconnect.tracker.service.*.update*(..)) ||
    execution(* com.ua.teamconnect.tracker.service.*.delete*(..)) ||
    execution(* com.ua.teamconnect.tracker.service.*.create*(..)) ||
    execution(* com.ua.teamconnect.tracker.repository.*.save*(..)) ||
    execution(* com.ua.teamconnect.tracker.repository.*.delete*(..))
    """)
    public void afterUpdate(JoinPoint joinPoint) {
        logger.info("Method {}.{}() completed successfully",
            findCallingClassName(joinPoint),
            joinPoint.getSignature().getName()
        );
    }
}
