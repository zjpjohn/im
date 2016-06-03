package com.shiku.mianshi.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import cn.xyz.commons.support.spring.SpringBeansUtils;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.service.KTipsServiceImpl;

import com.google.common.collect.Maps;
import com.shiku.mianshi.ResponseUtil;
import com.shiku.mianshi.controller.AdminController;

@WebFilter(filterName = "authorizationFilter", urlPatterns = { "/*" }, initParams = { @WebInitParam(name = "enable", value = "true") })
public class AuthorizationFilter implements Filter {
	private JedisPool jedisPool;
	private Map<String, String> requestUriMap;
	private AuthorizationFilterProperties properties;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {

		if (null == jedisPool || null == requestUriMap || null == properties) {
			requestUriMap = Maps.newHashMap();
			jedisPool = SpringBeansUtils.getBean("jedisPool");
			properties = SpringBeansUtils.getContext().getBean(
					AuthorizationFilterProperties.class);

			for (String requestUri : properties.getRequestUriList()) {
				requestUriMap.put(requestUri, requestUri);
			}
		}

		// if (!enable) {
		// arg2.doFilter(arg0, arg1);
		// return;
		// }

		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;

		String accessToken = request.getParameter("access_token");
		String requestUri = request.getRequestURI();

		// DEBUG**************************************************DEBUG
		StringBuilder sb = new StringBuilder();
		sb.append("请求：" + request.getRequestURI());
		Map<String, String[]> paramMap = request.getParameterMap();
		if (!paramMap.isEmpty())
			sb.append("?");
		for (String key : paramMap.keySet()) {
			sb.append(key).append("=").append(paramMap.get(key)[0]).append("&");
		}
		System.out.println(sb.toString());
		System.out.println("Content-Type：" + request.getContentType());
		System.out.println("User-Agent：" + request.getHeader("User-Agent"));
		System.out
				.println("**************************************************");
		// DEBUG**************************************************DEBUG

		// 如果访问的是控制台或资源目录
		if (requestUri.startsWith("/console")
				|| requestUri.startsWith("/pages")) {
			Object obj = request.getSession().getAttribute(
					AdminController.LOGIN_USER_KEY);
			// 用户已登录或访问资源目录或访问登录页面
			if (null != obj || requestUri.startsWith("/pages")
					|| requestUri.startsWith("/console/login")
					|| requestUri.startsWith("/console/logout"))
				arg2.doFilter(arg0, arg1);
			else
				response.sendRedirect("/console/login");
		} else {
			// 需要登录
			if (isNeedLogin(request.getRequestURI())) {
				// 请求令牌是否包含
				if (StringUtil.isEmpty(accessToken)) {
					int tipsKey = 1030101;
					renderByTipsKey(response, tipsKey);
				} else {
					String userId = getUserId(accessToken);
					// 请求令牌是否有效
					if (null == userId) {
						int tipsKey = 1030102;
						renderByTipsKey(response, tipsKey);
					} else {
						ReqUtil.setLoginedUserId(Integer.parseInt(userId));
						arg2.doFilter(arg0, arg1);
					}
				}
			} else {
				String userId = getUserId(accessToken);
				if (null != userId) {
					ReqUtil.setLoginedUserId(Integer.parseInt(userId));
				}
				arg2.doFilter(arg0, arg1);
			}
		}
	}

	private boolean isNeedLogin(String requestUri) {
		return !requestUriMap.containsKey(requestUri.trim());
	}

	private String getUserId(String _AccessToken) {
		Jedis resource = jedisPool.getResource();
		String userId = null;

		try {
			userId = resource.get(String.format("at_%1$s", _AccessToken));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.returnResource(resource);
		}

		return userId;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// enable = Boolean.parseBoolean(arg0.getInitParameter("enable"));
		// requestUriMap = Maps.newHashMap();
		// jedisPool = SpringBeansUtils.getBean("jedisPool");
		// properties =
		// SpringBeansUtils.getContext().getBean(AuthorizationFilterProperties.class);
		//
		// for (String requestUri : properties.getRequestUriList()) {
		// requestUriMap.put(requestUri, requestUri);
		// }
	}

	private static final String template = "{\"resultCode\":%1$s,\"resultMsg\":\"%2$s\"}";

	private static void renderByTipsKey(ServletResponse response, int tipsKey) {
		String tipsValue = KTipsServiceImpl.getTipsValue(tipsKey);
		String s = String.format(template, tipsKey, tipsValue);

		ResponseUtil.output(response, s);
	}

}
