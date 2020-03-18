package com.usermanagement.metrics;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import com.google.common.base.Stopwatch;
import com.timgroup.statsd.StatsDClient;

@Aspect
public class MethodProfiler {

	private final StatsDClient statsDClient;

	private static final Logger logger = LogManager.getLogger(MethodProfiler.class);

	public MethodProfiler(StatsDClient statsDClient) {
		this.statsDClient = statsDClient;
	}

	@Pointcut("execution(* com.usermanagement.controllers..*(..))")
	public void restServiceMethods() {
	}

	@Around("restServiceMethods()")
	public Object loggerProfile(ProceedingJoinPoint pjp) throws Throwable {

		// execute the method, record the result and measure the time
		logger.info("Method Entered: " + pjp.getSignature().getName());
		Object output = pjp.proceed();

		logger.info("Method Exited: " + pjp.getSignature().getName());
		// return the recorded result
		return output;
	}

	@Around("restServiceMethods()")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {
		// execute the method, record the result and measure the time
		logger.info("Start stopwatch ");
		Stopwatch stopwatch = Stopwatch.createStarted();
		Object output = pjp.proceed();
		stopwatch.stop();
		// send the recorded time to statsd
		statsDClient.recordExecutionTime(pjp.getSignature().getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
		// return the recorded result
		return output;
	}

	@Before("restServiceMethods()")
	public void countEndpointCall(JoinPoint joinPoint) {
		// Calling the statsDClient and incrementing count by 1 for respective endpoint.
		// joinPoint.getSignature().getName() returns the name of the method for which
		// this AOP method is called
		logger.info("Increment count for endpoint "+joinPoint.getSignature().getName());
		statsDClient.increment(joinPoint.getSignature().getName());
	}


}
