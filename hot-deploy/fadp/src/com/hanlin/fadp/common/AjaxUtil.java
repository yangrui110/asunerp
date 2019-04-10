package com.hanlin.fadp.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import groovy.json.JsonOutput;
@SuppressWarnings("rawtypes")
/*
 * 为了兼容ie8 这里返回的json全都是字符串
 */
public class AjaxUtil {

	public static void writeErrorJsonToResponse(String error, HttpServletResponse response) throws IOException {
		writeJsonToResponse("{\"error\":\"" + error + "\"}", response);
	}

	
	public static void writeJsonToResponse(Map obj, HttpServletResponse response) throws IOException {
		writeJsonToResponse(JsonOutput.toJson(obj), response);
	}

	public static void writeJsonToResponse(List obj, HttpServletResponse response) throws IOException {
		writeJsonToResponse(JsonOutput.toJson(obj), response);
	}

	public static void writeJsonToResponse(String json, HttpServletResponse response) throws IOException {
//		response.setContentType("application/json");
		response.getWriter().print(json);
	}
	
	public static void writeJsonToResponse(String json,HttpServletRequest repuest,  HttpServletResponse response) throws IOException {
		String agent = getBrowserName(repuest);
		if(!agent.startsWith("ie")){
			response.setContentType("application/json");
		}
		response.getWriter().print(json);
	}

	public static String getBrowserName(HttpServletRequest repuest) {
		String agent=repuest.getHeader("User-Agent");
		if (agent.indexOf("msie 7") > 0) {
			return "ie7";
		} else if (agent.indexOf("msie 8") > 0) {
			return "ie8";
		} else if (agent.indexOf("msie 9") > 0) {
			return "ie9";
		} else if (agent.indexOf("msie 10") > 0) {
			return "ie10";
		} else if (agent.indexOf("msie") > 0) {
			return "ie";
		} else if (agent.indexOf("opera") > 0) {
			return "opera";
		} else if (agent.indexOf("opera") > 0) {
			return "opera";
		} else if (agent.indexOf("firefox") > 0) {
			return "firefox";
		} else if (agent.indexOf("webkit") > 0) {
			return "webkit";
		} else if (agent.indexOf("gecko") > 0 && agent.indexOf("rv:11") > 0) {
			return "ie11";
		} else {
			return "Others";
		}
	}
}
