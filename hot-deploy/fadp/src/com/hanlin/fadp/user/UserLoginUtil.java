package com.hanlin.fadp.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.crypto.HashCrypt;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilIO;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.login.LoginServices;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericDelegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.entity.util.EntityUtilProperties;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.ServiceUtil;
import com.hanlin.fadp.utils.HttpResponseHelper;
import com.hanlin.fadp.webapp.control.LoginWorker;

import javolution.util.FastMap;

public class UserLoginUtil {
    public static final String module = UserLoginUtil.class.getName();

    /**
     * 获取免登key
     */
    public static String getExternalLoginKey(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> returnMap = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        request.setAttribute("userLogin", userLogin);
        request.getSession().removeAttribute(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR);
        returnMap.put("externalLoginKey", LoginWorker.getExternalLoginKey(request));
        HttpResponseHelper.writeJson(response, returnMap);
        return "success";
    }

    /**
     * 检查登陆
     */
    public static String ajaxCheckLogin(HttpServletRequest request, HttpServletResponse response) {
        if (false) {
            return "success";
        }
        Map<String, Object> returnMap = FastMap.newInstance();

        String s = LoginWorker.checkLogin(request, response);
        if (!"success".equalsIgnoreCase(s)) {
            returnMap.put("needLogin", true);
            HttpResponseHelper.writeJson(response, returnMap);
        }
        return "success";


    }

    /**
     * angular 页面
     */
    public static String www(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, IOException {
        String thisRequestUri = (String) request.getAttribute("thisRequestUri");
        String requestURI = request.getRequestURI();
        String baseUrl = requestURI.substring(0, requestURI.lastIndexOf("/" + thisRequestUri));
        String wwwFileName = requestURI.substring(baseUrl.length());
        if (wwwFileName == null || wwwFileName.equals("/" + thisRequestUri + "/")) {
            //正常访问首页
            wwwFileName = "/" + thisRequestUri + "/index.html";

        } else if (wwwFileName.indexOf('.') < 0) {
            //非首页刷新。
            thisRequestUri += "/";
            int len = wwwFileName.split("/").length;
            for (int i = 0; i < len - 2; i++) {
                thisRequestUri = "../" + thisRequestUri;
            }
            response.sendRedirect(thisRequestUri);
            return "success";
        }
        String mimeType = UtilHttp.getContentTypeByFileName(wwwFileName);

        response.setContentType(mimeType);
        URL resource = request.getServletContext().getResource(wwwFileName);

        if (wwwFileName.endsWith("index.html")) {

            String text = UtilIO.readString(resource.openStream());
//            String newBase="<base href=\"" + baseUrl+"/"+thisRequestUri + "/\">";
            String newBase = "<base href=\"./\">";
            String html = text.replace("<base href=\"/\">", newBase).replace("<base href=\"./\">", newBase);
            PrintWriter writer = response.getWriter();
            writer.print(html);
            writer.close();
        } else {
            UtilIO.copy(resource.openStream(), true, response.getOutputStream(), true);
        }
        return "success";
    }


    /**
     * 登陆
     */
    public static String ajaxLogin(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Map<String, Object> returnMap = loginService(request, response);
        HttpResponseHelper.writeJson(response, returnMap);
        return "success";
    }

    /**
     * 获取用户数据
     */
    public static String getUserData(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Map<String, Object> returnMap = loginService(request, response);
        HttpResponseHelper.writeJson(response, returnMap);
        return "success";
    }


    /**
     * 检查登陆
     */
    public static Map<String, Object> loginService(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        String msg = null;
        if (request.getSession().getAttribute("userLogin") == null) {
            msg = LoginWorker.login(request, response);
        }
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));
        GenericValue userLogin = UtilGenerics.cast(parameters.get("userLogin"));

        request.setAttribute("userLogin", userLogin);
        String id = request.getSession().getId();

        Map<String, Object> returnMap = FastMap.newInstance();
        if (UtilValidate.areEqual(msg, "error")) {
            returnMap.put("error", parameters.get("_ERROR_MESSAGE_"));
        } else {
            returnMap = FastMap.newInstance();
            returnMap.putAll(userLogin);
            returnMap.put("jsessionid", id);
            Map<String, Object> map = getPermission(delegator, userLogin.getString("userLoginId"));
            returnMap.put("permissions", map.get("permissions"));
            returnMap.put("projectPermission", map.get("projectPermission"));

            //部门信息
            GenericValue firstDept = EntityQuery.use(delegator).from("DeptUserView").where("userLoginId", userLogin.get("userLoginId")).queryFirst();
            if (UtilValidate.isNotEmpty(firstDept)) {
                returnMap.put("deptId", firstDept.get("deptId"));
                returnMap.put("deptName", firstDept.get("deptName"));
            }

        }
        return returnMap;
    }

    private static Map<String, Object> getPermission(Delegator delegator, String userLoginId) throws GenericEntityException {
        List<GenericValue> permissioins = EntityQuery.use(delegator).from("UserPermissionView").where("userLoginId", userLoginId).queryList();
        Set<String> set = new HashSet<>();
        Map<String, Set<String>> map = new HashMap<>();
        for (GenericValue gv : permissioins) {
            set.add(gv.getString("permissionId"));
            if (gv.getAllFields().containsKey("projectId") && UtilValidate.isNotEmpty(gv.getString("projectId"))) {
                if (UtilValidate.isEmpty(map.get(gv.getString("projectId")))) {
                    map.put(gv.getString("projectId"), UtilMisc.toSet(gv.getString("permissionId")));
                } else {
                    map.get(gv.getString("projectId")).add(gv.getString("permissionId"));
                }
            }
        }
        return UtilMisc.toMap("permissions", set, "projectPermission", map);
    }

    //退出登陆
    public static String logout(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        LoginWorker.logout(request, response);
        HttpResponseHelper.writeJson(response, FastMap.<String, Object>newInstance());

        return "success";
    }

    /**
     * 登陆
     * 
     */
    public static String signup(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        String username = (String) parameters.get("userLoginId");
        String currentPassword = (String) parameters.get("password");
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));
        GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", username).queryOne();
        if (userLogin == null) {
            //可以注册

            String userNextId = delegator.getNextSeqId("UserLogin");

            userLogin = delegator.makeValidValue("UserLogin", parameters);

            userLogin.setString("hasAuth", "N");//默认不开通，审核通过后开通。
            boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security.properties", "password.encrypt", delegator));

            userLogin.set("currentPassword", useEncryption ? HashCrypt.cryptUTF8(LoginServices.getHashType(), null, currentPassword) : currentPassword);

            userLogin.create();
            request.setAttribute("USERNAME", username);
            request.setAttribute("PASSWORD", currentPassword);
            ajaxLogin(request, response);
        } else {
            Map<String, Object> returnMap = FastMap.newInstance();

            returnMap.put("error", "手机号被占用");
            HttpResponseHelper.writeJson(response, returnMap);

        }
        return "success";
    }

    /**
     * 修改密码
     */
    public static String changePassword(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Map<String, Object> returnMap = FastMap.newInstance();

        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        GenericValue userLogin = UtilGenerics.cast(parameters.get("userLogin"));

        LocalDispatcher dispatcher = UtilGenerics.cast(parameters.get("dispatcher"));
        String username = userLogin.getString("userLoginId");
        String password = (String) parameters.get("password");
        Map<String, Object> inMap = UtilMisc.<String, Object>toMap("login.username", username, "login.password", password, "locale", UtilHttp.getLocale(request));
        inMap.put("userLoginId", username);
        inMap.put("currentPassword", password);
        inMap.put("newPassword", parameters.get("newPassword"));
        inMap.put("newPasswordVerify", parameters.get("newPasswordVerify"));
        String error = null;
        try {
            Map<String, Object> resultMap = dispatcher.runSync("updatePassword", inMap);
            if (ServiceUtil.isError(resultMap)) {
                error = "修改密码失败";
            }
        } catch (GenericServiceException e) {
            error = "修改密码失败";
            Debug.logError(error + e.getMessage(), module);

        }
        if (error != null) {
            returnMap.put("error", error);
        }

        HttpResponseHelper.writeJson(response, returnMap);
        return "success";
    }

}
