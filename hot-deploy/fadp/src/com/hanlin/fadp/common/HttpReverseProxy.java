package com.hanlin.fadp.common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilValidate;
/**
 * 代理相关工具类
 * @author 陈林
 *
 */
public class HttpReverseProxy {
	/**
	 * 获取被代理的请求的客户端真实ip
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (UtilValidate.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (UtilValidate.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}	
	public static String proxyLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sessionId = UtilHttp.getSessionId(request);
		String contextPath = request.getContextPath();
		String url = request.getRequestURL().toString();
		String pathInfo=request.getPathInfo();
		String parentPath=url.substring(0,url.indexOf(pathInfo)+1);
		System.out.println(contextPath);
		System.out.println(url);
		System.out.println(pathInfo);
		System.out.println(parentPath);
		URL realUrl = new URL(parentPath+"main?USERNAME=admin&PASSWORD=ofbiz");
		HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
		String requestCookie = request.getHeader("Cookie");
		if (requestCookie == null || !requestCookie.contains("JSESSIONID")) {
			requestCookie += (";JSESSIONID=" + sessionId);
		}
		connection.setRequestProperty("Cookie", requestCookie);
		connection.connect();
		// 获取所有响应头字段
		Map<String, List<String>> map = connection.getHeaderFields();
		List<String> setCookies = map.get("Set-Cookies");
		if (setCookies != null) {
			for (String cookie : setCookies) {
				response.setHeader("Set-Cookies", cookie);
				if (cookie.contains("JSESSIONID")) {
					sessionId = cookie.split("JSESSIONID=")[1];
				}
			}
		}

		System.out.println(sessionId);
		response.sendRedirect(parentPath+"main;jsessionid=" + sessionId);

		return "success";
	}
}
