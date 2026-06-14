package com.ua.teamconnect.tracker.aop;

import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

abstract class AbstractLoggingAspect {

    String findCallingClassName(JoinPoint joinPoint) {
        var target = joinPoint.getTarget();
        if (target == null) return joinPoint.getSignature().getDeclaringTypeName();
        var stream = Arrays.stream(target.getClass().getInterfaces());
        stream = Stream.concat(stream, Stream.of(
            target.getClass().getSuperclass(),
            target.getClass()
        ));
        return stream
            .filter(implementedInterface -> Arrays.stream(implementedInterface.getMethods())
                .anyMatch(method -> isMethodMatches(joinPoint, method)))
            .findFirst()
            .map(Class::getName)
            .orElseGet(() -> joinPoint.getSignature().getDeclaringTypeName());
    }

    private boolean isMethodMatches(JoinPoint joinPoint, Method method) {
        if (!Objects.equals(joinPoint.getSignature().getName(), method.getName())) return false;
        if (joinPoint.getArgs().length != method.getParameterCount()) return false;
        for (int i = 0; i < method.getParameterCount(); i++) {
            var parameterType = method.getParameterTypes()[i];
            var arg = joinPoint.getArgs()[i];
            if (arg != null && !parameterType.isAssignableFrom(arg.getClass())) return false;
        }
        return true;
    }

}
