package com.fadp.jiyuan.user;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class PostData {

    /**
     * <p>获取post传递过来的json数据</p>
     * */
    public static Map getJson(HttpServletRequest request, HttpServletResponse response) throws IOException {

        InputStream stream=request.getInputStream();
        byte[] bys=new byte[1024];
        int len=-1;
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        while ((len=stream.read(bys))!=-1){
            out.write(bys,0,len);
        }
        byte[] rs=out.toByteArray();
        String json=new String(rs);
        return JSONObject.fromObject(json);
    }
}
