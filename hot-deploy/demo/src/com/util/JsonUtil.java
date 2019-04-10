package com.util;

import com.result.ResponseEntity;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @autor 杨瑞
 * @date 2018/11/23 15:10
 */
public class JsonUtil {

    public static String parseJson(Object object){

        JSONObject json= JSONObject.fromObject(object);
        return json.toString();
    }

    /**由于前端都需要返回固定格式，所以需要统一调用*/
    public static void writeJsonResponseObject(HttpServletResponse response,Object data){

        try {
            response.getWriter().write(parseJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String writeError(HttpServletResponse response,String msg) throws IOException {
        JSONObject object=new JSONObject();
        object.put("error",msg);
        response.getWriter().write(object.toString());
        return "success";
    }
}
