package com.hanlin.fadp.common;

import java.util.Map;

import com.hanlin.fadp.base.util.UtilValidate;

public class MapUtil {
	public static void putIfMiss(Map<String, Object> map,String key,String defaultValue){
		Object object = map.get(key);
		if(UtilValidate.isEmpty(object)){
			map.put(key, defaultValue);
		}
	}

}
