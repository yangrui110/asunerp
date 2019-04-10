package com.hanlin.fadp.utils;

import java.util.HashMap;
import java.util.Map;

public class Constant {
	public static Map<String ,String> mimeMap=new HashMap<String, String>();
	static{
		mimeMap.put("doc","application/msword");
		mimeMap.put("xls","application/vnd.ms-excel");
		mimeMap.put("ppt","application/vnd.ms-powerpoint");
		mimeMap.put("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		mimeMap.put("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		mimeMap.put("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
		mimeMap.put("dwg","application/x-autocad");
		mimeMap.put("rar","application/x-rar-compressed");
		mimeMap.put("vsd","application/vnd.visio");
		
		
	}
}
 