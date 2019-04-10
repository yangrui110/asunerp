package com.hanlin.fadp.utils;

import com.hanlin.fadp.base.util.Debug;
import groovy.json.JsonOutput;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class HttpResponseHelper {
    public static final String module=HttpResponseHelper.class.getName();
    public static void writeJson(HttpServletResponse response, Map<String,Object> obj){
        Debug.logInfo("\n发送数据："+obj,module);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print(JsonOutput.toJson(obj));
            writer.close();
        } catch (IOException e) {
            Debug.logInfo(e,module);
        }
    }
}
