package com.util;

import com.hanlin.fadp.base.util.UtilHttp;
import com.util.constant.PageConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 杨瑞
 * @date 2018-11-23
 *
 * 此类主要处理前端传过来的page和size参数
 * */
public class DealPageRequest {

    private static int dealMap(Map parameters,String data){

        Object page =parameters.get(data);
        if(page==null)
            return 0;
        else {
            if(page instanceof  Integer)
                return (Integer)page;
            else if(page instanceof  String)
                return Integer.parseInt((String)page);
            else return 0;
        }
    }

    private static int dealRequest(HttpServletRequest request,String data){
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        return dealMap(parameters,data);
    }

    public static int getPage(HttpServletRequest request){
        return dealRequest(request, PageConstant.PAGE);
    }

    public static int getSize(HttpServletRequest request){

        int rs=dealRequest(request,PageConstant.SIZE);
        if(rs==0)
            return 10;
        else return rs;
    }
}
