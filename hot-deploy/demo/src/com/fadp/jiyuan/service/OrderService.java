package com.fadp.jiyuan.service;

import com.hanlin.fadp.common.AjaxCURD;
import com.hanlin.fadp.entity.GenericEntityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @autor 杨瑞
 * @date 2018/11/30 16:44
 */
public class OrderService {

    public static String getOrderPageData(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {

        return AjaxCURD.getPageData(request,response);
    }
}
