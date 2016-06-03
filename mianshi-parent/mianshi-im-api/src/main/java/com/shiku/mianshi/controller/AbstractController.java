package com.shiku.mianshi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public abstract class AbstractController {

	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}

	/**
	 * 获取登录用户的用户索引
	 * 
	 * @return 用户索引
	 */
	// public int getUserId() {
	// Object userId = getRequest().getAttribute("userId");
	// return null == userId ? 0 : Integer.parseInt(userId.toString());
	// }

	public String getRequestIp() {
		HttpServletRequest request = getRequest();
		String requestIp = request.getHeader("x-forwarded-for");

		if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
			requestIp = request.getHeader("X-Real-IP");
		}
		if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
			requestIp = request.getHeader("Proxy-Client-IP");
		}
		if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
			requestIp = request.getHeader("WL-Proxy-Client-IP");
		}
		if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
			requestIp = request.getHeader("HTTP_CLIENT_IP");
		}
		if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
			requestIp = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
			requestIp = request.getRemoteAddr();
		}
		if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
			requestIp = request.getRemoteHost();
		}

		return requestIp;
	}
}