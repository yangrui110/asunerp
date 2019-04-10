package com.fadp.jiyuan.service;

import com.fadp.file.ImagePath;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.AjaxCURD;
import com.hanlin.fadp.common.AjaxUtil;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericPK;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.hanlin.fadp.common.AjaxCURD.findListByJsonCondition;

public class ActiveService {

    /***/
    public static String getActivesPageData(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
        Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
        Delegator delegator = ImagePath.getDelegator(attrMap, request);
        Map<String,Object> maps=findListByJsonCondition(delegator, attrMap);
        List<Map<String,Object>> lists= (List) maps.get("list");
        for (Map map:lists) {
            Object os=map.get("hdtp");
            if(os!=null) {
                try {
                    map.put("hdtp", ImagePath.getPath((String) os));
                }catch (Exception e){
                    e.printStackTrace();
                    map.put("hdtp","");
                }
            }
        }
        AjaxUtil.writeJsonToResponse(maps,response);
        return "success";
    }
    public static String getActiveOne(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
        Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
        Delegator delegator = ImagePath.getDelegator(attrMap, request);
        String entityName = (String) attrMap.get("entityName");
        GenericPK pk =AjaxCURD.parsePK(delegator, entityName, (Map<String, Object>) attrMap.get("PK"));
        if (UtilValidate.isNotEmpty(pk)) {
            GenericValue one = EntityQuery.use(delegator).from(entityName).where(pk).queryOne();
            Object os=one.get("hdtp");
            if(os!=null) {
                try {
                    one.put("hdtp", ImagePath.getPath((String) os));
                }catch (Exception e){
                    e.printStackTrace();
                    one.put("hdtp","");
                }
            }
            AjaxUtil.writeJsonToResponse(one,response);
        } else {
            AjaxUtil.writeErrorJsonToResponse("数据不存在", response);
        }
        return "success";
    }

}
