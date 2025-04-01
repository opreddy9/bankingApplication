package com.copart.bankingApplication.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.copart.bankingApplication.*.*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("========>>>>> i am inside the AOP Around which i wrote for purpose of logging");
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("Incoming request to {} with arguments: {}", methodName, Arrays.toString(args));

        Object result = joinPoint.proceed();

        logger.info("Response from {}: {}", methodName, result);
        System.out.println("=======>>>>> this the end of the AOP for logging");

        return result;
    }
}
