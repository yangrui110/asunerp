package com.fadp.jiyuan.service;

import com.fadp.email.SendMail;
import com.fadp.msg.SendMsg;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.common.AjaxUtil;
import com.hanlin.fadp.entity.GenericDelegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.util.VerifyCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2018/11/27 14:48
 */
public class OtherService {

    /**生成验证码*/
    public static String getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        String code= VerifyCode.generateVerifyCode(4);
        request.getSession().setAttribute("vcode", code);
        System.out.println("生成验证码的sessionId="+request.getSession().getId());
        int w=140,h=40;
        VerifyCode.outputImage(w, h, response.getOutputStream(), code);
        return "success";
    }

    /**获取邮箱随机码*/
    public static String getRandCode(final HttpServletRequest request,HttpServletResponse response) throws Exception {
        final String email= (String) request.getAttribute("email");

        System.out.println("获取到的邮箱:"+email);
        final String code=getRandm(6);
        request.getSession().setAttribute("emailCode",code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object object=request.getAttribute("xm");
                String name="";
                if(object!=null)
                    name= (String) object;
                try {
                    SendMail.sendMail(code,email,name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        AjaxUtil.writeJsonToResponse(UtilMisc.toMap("result",true),response);
        System.out.println("随机邮箱验证码:"+code);
        return "success";
    }
    /**
     * 获取随机手机短信验证码
     * */
    public static String getPhoneRandm(HttpServletRequest request,HttpServletResponse response) throws IOException, GenericEntityException {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        if(!UfKhglService.isCorrectCode(request,response,"code","vcode","图形码填写不正确"))
            return "success";
        String phone= (String) parameters.get("mobile");
        System.out.println("获取到的手机号是:"+phone);
        String code=getRandm(6);

        SendMsg.sendPhoneCode("验证码："+code,phone);
        request.getSession().setAttribute("sphoneCode",code);
        AjaxUtil.writeJsonToResponse(UtilMisc.toMap("result",true),response);
        System.out.println("随机手机短信验证码:"+code);
        return "success";
    }

    /**
     * 响应注册用户时的手机验证码
     * */
    public static String getPass(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException, IOException {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));

        String phone= (String) parameters.get("mobile");
        Map condition=new HashMap();
        condition.put("sj",phone);
        List<GenericValue> value=delegator.findList("UfKhglView", EntityCondition.makeCondition(condition),null,null,null,false);
        if(value.size()>0){
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","该手机号码已经注册过了哦，请直接登陆！"),response);
            return "success";
        }else getPhoneRandm(request,response);
        return "success";
    }

    private static String getRandm(int length){
        int[] zd=new int[]{0,1,2,3,4,5,6,7,8,9};
        StringBuffer buffer=new StringBuffer();
        for (int i=0;i<length;i++) {
            int ran=(int)(Math.random()*9);
            buffer.append(zd[ran]);
        }
        return buffer.toString();
    }
}
