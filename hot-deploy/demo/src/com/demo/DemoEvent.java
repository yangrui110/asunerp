package com.demo;

import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.AjaxCURD;
import com.hanlin.fadp.common.AjaxUtil;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import javolution.util.FastList;
import javolution.util.FastMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DemoEvent {
    // 保存或修改订单
    public static String saveOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String error = "";
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");

        Map orderData = (Map) parameters.get("orderData");
        List<Map> orderItemListData = (List<Map>) parameters.get("orderItemList");


        // 现保存订单，并得到订单id
        GenericValue demoOrder = AjaxCURD.parseValue(delegator, "DemoOrder", orderData);
        if (demoOrder.containsPrimaryKey()) {
            // 有主键代表更新
            // 更新时，先删除条目数据
            try {
                demoOrder.store();
                delegator.removeByCondition("DemoOrderItem", EntityCondition.makeCondition("orderId", demoOrder.get("orderId")));
            } catch (GenericEntityException e) {
                e.printStackTrace();
                error += "订单修改失败";
            }
        } else {
            // 新增
            demoOrder.setNextSeqId();
            // 给订单填充自增长主键
            try {
                demoOrder.create();
            } catch (GenericEntityException e) {
                e.printStackTrace();
                error += "订单创建失败";
            }
        }


        if (UtilValidate.isEmpty(error)) {
            List<GenericValue> toSaveOrderItemList = FastList.newInstance();
            // 保存订单条目
            for (Map orderItemData : orderItemListData) {
                GenericValue demoOrderItem = AjaxCURD.parseValue(delegator, "DemoOrderItem", orderItemData);
                // 给订单条目填充订单编号
                demoOrderItem.set("orderId", demoOrder.get("orderId"));
                demoOrderItem.setNextSeqId();
                toSaveOrderItemList.add(demoOrderItem);
            }
            try {
                // 批量保存订单条目
                delegator.storeAll(toSaveOrderItemList);
            } catch (GenericEntityException e) {
                e.printStackTrace();
                error += "订单条目保存失败";

            }
        }
        // 向前台返回数据
        Map returnMap = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(error)){
            returnMap.put("error", error);
        }
        AjaxUtil.writeJsonToResponse(returnMap, response);
        return "success";
    }
}
