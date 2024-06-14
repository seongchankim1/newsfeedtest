package com.sparta.newsfeed.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "RequestInfoAop")
@Aspect
@Component
@RequiredArgsConstructor
public class RequestInfoAop {


	@Pointcut("execution(* com.sparta.newsfeed.controller.CommentController.*(..))")
	private void comment() {}
	@Pointcut("execution(* com.sparta.newsfeed.controller.LikeController.*(..))")
	private void like() {}
	@Pointcut("execution(* com.sparta.newsfeed.controller.NewsfeedController.*(..))")
	private void newsfeed() {}
	@Pointcut("execution(* com.sparta.newsfeed.controller.UserController.*(..))")
	private void user() {}

	@Before("comment() || like() || newsfeed() || user()")
	public void logRequestInfo() {
		HttpServletRequest request =
			((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
				.getRequest();

		log.info("Request URL: {}", request.getRequestURL());
		log.info("HTTP Method: {}", request.getMethod());
	}
}
