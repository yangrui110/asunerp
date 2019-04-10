package com.fadp.jiyuan.service;

import com.fadp.file.ImagePath;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.AjaxCURD;
import com.hanlin.fadp.common.AjaxUtil;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityFindOptions;
import com.hanlin.fadp.entity.util.EntityListIterator;
import com.result.ResponseEntity;
import com.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.hanlin.fadp.common.AjaxCURD.findListByJsonCondition;

/**
 * @autor 杨瑞
 * @date 2018/11/28 9:11
 */
public class ProductService {

    public static void getPageProducts(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {

        /*
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        EntityListIterator values=delegator.find("UfCpxxView",null,null,null,null, new EntityFindOptions(true,EntityFindOptions.TYPE_SCROLL_SENSITIVE,EntityFindOptions.CONCUR_READ_ONLY,false));
        List results= values.getPartialList(0,10);
        JsonUtil.writeJsonResponseObject(response,new ResponseEntity(200,results));
        */
        Map<String, Object> attrMap = UtilHttp.getAttributeMap(request);
        Delegator delegator = ImagePath.getDelegator(attrMap, request);
        Map<String,Object> maps=findListByJsonCondition(delegator, attrMap);
        List<Map<String,Object>> lists= (List) maps.get("list");
        for (Map map:lists) {
            Object os=map.get("cpfj");
            if(os!=null) {
                try {
                    map.put("cpfj", ImagePath.getPath((String) os));
                }catch (Exception e){
                    e.printStackTrace();
                    map.put("cpfj","");
                }
            }
        }
        AjaxUtil.writeJsonToResponse(maps,response);
    }


}
