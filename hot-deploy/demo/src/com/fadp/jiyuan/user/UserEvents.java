package com.fadp.jiyuan.user;

import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.common.AjaxUtil;
import com.hanlin.fadp.entity.GenericDelegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.utils.HttpResponseHelper;
import com.hanlin.fadp.webapp.control.LoginWorker;
import org.apache.commons.codec.digest.Md5Crypt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2018/11/27 13:26
 */
public class UserEvents {
    /**
     * 用户登录的操作
     * 1.查询远程sqlServer中的数据
     * 2.登录本地h2数据库
     * */
    public static String ajaxLogin(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, IOException {
        response.setContentType("application/json");
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));
        boolean loginSuccess=false;
        // TODO: 登陆极元
        GenericValue value=getBaseValue(parameters,delegator,response);
        if(value!=null)
            loginSuccess=true;
        else {
            return "success";
        }

        if(loginSuccess){
            //
            GenericValue userLoginValue = delegator.makeValue("UserLogin");
            String userLoginId= ""+ value.get("id");
            String name= (String) value.get("xm");
            userLoginValue.set("userLoginId",userLoginId);
            userLoginValue.set("userName",name);
            delegator.createOrStore(userLoginValue);
            LoginWorker.loginUserWithUserLoginId(request, response, userLoginId);

            HttpResponseHelper.writeJson(response, userLoginValue);
        }

        return "success";
    }

    /**
     *根据手机号或者身份证号，并结合密码，获取用户信息
     * 手机号：11位
     * 护照号：9位
     * 身份证号：18位或者其它
     * @return 返回为null,代表账号或者密码不正确
     */
    public static  GenericValue getBaseValue(Map parameters, GenericDelegator delegator,HttpServletResponse response) throws GenericEntityException, IOException {
        Object username=parameters.get("userName");
        Object password=parameters.get("passWord");
        if(username!=null&&password!=null){
            EntityCondition condition1=EntityCondition.makeCondition("sj",username);
            EntityCondition condition2=EntityCondition.makeCondition("sfzhm",username);
            List<EntityCondition> orList=new ArrayList<>();
            orList.add(condition1);
            orList.add(condition2);
            EntityCondition base=EntityCondition.makeCondition(orList, EntityOperator.OR);
            List<GenericValue> ls= delegator.findList("BaseUfKhgl",base,null,null,null,false);
            if(ls.size()==1){
                //验证密码的合法性
                if(isCorrect((String) password,ls.get(0)))
                    return ls.get(0);
                else
                    AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","密码填写不正确"),response);
                return null;
            }else {
                AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","账号不存在"),response);
                return null;
            }
        }else {
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","没有输入账号或者密码"),response);
            return null;
        }
    }

    private static boolean isCorrect(String loginPass,GenericValue user){
        Object encryPass=user.get("mm");
        if(encryPass!=null){
            String p=(String)encryPass;
            int index=p.lastIndexOf("$");
            if(index<3)
                return false;
            String salt=p.substring(0,index+1);
            String password=Md5Crypt.md5Crypt(loginPass.getBytes(),salt);
            if(password.equals(p))
                return true;
        }
        return false;
    }

    public static boolean isEmpty(String str){
        if(str==null)
            return true;
        else if("".equals(str))
            return true;
        else return false;
    }
}
