package com.squirrel.persons.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAdvice {
    private static final Logger LOGGER = LogManager.getLogger(ExecutionTimeAdvice.class);

    @Around("@annotation(com.squirrel.persons.aspect.TrackExecutionTime)")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object object = point.proceed();
        long endTime = System.currentTimeMillis();
        LOGGER.debug("Class Name: "+ point.getSignature().getDeclaringTypeName() +". Method Name: "+ point.getSignature().getName() + ". Time taken for Execution is : " + (endTime-startTime) +"ms");
        return object;
    }
}