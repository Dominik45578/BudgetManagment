package com.kowallo.accounts.budget.summary.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class SummaryLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(SummaryLoggingAspect.class);

    @Pointcut("within(com.kowallo.accounts.budget.summary.service.*)")
    public void summaryServiceMethods() {}

    @Before("summaryServiceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Action: Executing method {}() with arguments: {}", 
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "summaryServiceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Action: Method {}() executed successfully. Returned: {}", 
                joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "summaryServiceMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Action: Exception in method {}() with cause: {}", 
                joinPoint.getSignature().getName(), exception.getMessage());
    }
}
