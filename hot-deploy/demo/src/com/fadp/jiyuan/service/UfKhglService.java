package com.fadp.jiyuan.service;

import com.fadp.jiyuan.user.PostData;
import com.fadp.jiyuan.user.UserEvents;
import com.fadp.msg.SendMsg;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.AjaxCURD;
import com.hanlin.fadp.common.AjaxUtil;
import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityJoinOperator;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.result.ResponseEntity;
import com.util.JsonUtil;
import localhost.services.ModeDateService.ModeDateServiceLocator;
import localhost.services.ModeDateService.ModeDateServicePortType;
import org.apache.commons.codec.digest.Md5Crypt;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @autor 杨瑞
 * @date 2018/11/26 17:02
 */
public class UfKhglService {

    /**
     * <p>绑定微信</p>
     * */
    public static String bindWx(HttpServletRequest request,HttpServletResponse response) throws IOException, GenericEntityException {
        Map result= PostData.getJson(request,response);
        Map condition=new HashMap();
        condition.put("sj",result.get("sj"));
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<GenericValue> value=delegator.findList("BaseUfKhglView", EntityCondition.makeCondition(condition),null,null,null,false);
        if(value.size()==0)
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","该手机号不存在与系统中"),response);
        return "success";
    }

    public static String getKhglPageData(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List lists=delegator.findList("BaseUfKhglView",null,null,null,null,false);
        System.out.println(lists.size());
        JsonUtil.writeJsonResponseObject(response,new ResponseEntity(200,lists));
        return "success";
    }
    /**
     * 通过手机号找回密码
     * @return 返回的是用户数据库密码
     * */
    public static String findPassword(HttpServletRequest request,HttpServletResponse response) throws IOException, GenericEntityException {
        if(!isCorrectCode(request,response,"phoneCode","sphoneCode","短信验证码填写不正确"))
            return "success";
        if(!isCorrectCode(request,response,"code","vcode","图形码填写不正确"))
            return "success";
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));
        Map condition=new HashMap();
        String phone= (String) parameters.get("phone");
        condition.put("sj",phone);
        if(phone.length()<6){
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","手机号码长度不正确"),response);
            return "success";
        }
        List<GenericValue> value=delegator.findList("BaseUfKhglView", EntityCondition.makeCondition(condition),null,null,null,false);
        if(value.size()==0) {
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","该手机号并不存在于系统中"),response);
            return "success";
        }
        GenericValue user=value.get(0);
        AjaxUtil.writeJsonToResponse(UtilMisc.toMap("id",user.getString("id")),response);
        return "success";
    }
    public static String bindEmail(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException, IOException, ServiceException, DocumentException {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        Map<String, Object> fieldMap = (Map<String, Object>) parameters.get("fieldMap");
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));
        String entityName = (String) parameters.get("entityName");
        GenericValue PK = AjaxCURD.parseValue(delegator, entityName, (Map<String, Object>) parameters.get("PK"));
        if (PK.size() == 0) {
            return null;
        }
        GenericPK pk=PK.getPrimaryKey();
        if (UtilValidate.isNotEmpty(pk)) {
            GenericValue one = EntityQuery.use(delegator).from(entityName).where(pk).queryOne();
            if (one != null) {
                if(isCorrectCode(request,response,"code","emailCode","邮箱验证码填写不正确")) {
                    String m = (String) parameters.get("email");
                    one.set("cyyj", m);
                    Map maps=new HashMap();
                    maps.put("id",PK.get("id"));
                    maps.put("cyyj",m);
                   return updateUser(maps,response);
                    //delegator.store(one);
                }
            }
        }
        return "success";
    }

    public static String upPs(HttpServletRequest request,HttpServletResponse response) throws ServiceException, DocumentException, IOException {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));
        String entityName = (String) parameters.get("entityName");
        String ne= (String) parameters.get("passWord");
        GenericValue PK = AjaxCURD.parseValue(delegator, entityName, (Map<String, Object>) parameters.get("PK"));
        if(ne.length()<6){
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","密码长度小于6位数"),response);
            return "success";
        }
        Map map=new HashMap();
        map.put("mm",encrypt(ne));
        map.put("id",""+PK.get("id"));
        updateUser(map,response);
        return "success";
    }
    /**更新用户密码*/
    public static  String updatePassword(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException, IOException, ServiceException, DocumentException {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        Map<String, Object> fieldMap = (Map<String, Object>) parameters.get("fieldMap");
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));
        String entityName = (String) parameters.get("entityName");
        GenericValue PK = AjaxCURD.parseValue(delegator, entityName, (Map<String, Object>) parameters.get("PK"));
        if (PK.size() == 0) {
            return null;
        }
        GenericPK pk=PK.getPrimaryKey();
        if (UtilValidate.isNotEmpty(pk)) {
            GenericValue one = EntityQuery.use(delegator).from(entityName).where(pk).queryOne();
            if(one!=null){
                //1.判断输入的原始密码是否正确
                //2.获取原始密码,获取新密码，判断原始和新的密码是否相等
                //3.不相等，修改为新密码，相等的话返回提示信息
                String mm= (String) one.get("mm");
                String ne= (String) parameters.get("passWord");

                if(mm.length()<4){
                    AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","原始密码小于4位数"),response);
                    return "success";
                }
                int pos=mm.lastIndexOf("$");
                String salt=mm.substring(0,pos+1);
                //旧密码相关开始
                if(parameters.get("old")!=null){
                    String old= (String) parameters.get("old");
                    String oldResult=Md5Crypt.md5Crypt(old.getBytes(),salt);
                    if(!mm.equals(oldResult)){
                        AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","原始密码输入不正确"),response);
                        return "success";
                    }
                }
                //旧密码相关结束
                String result= Md5Crypt.md5Crypt(ne.getBytes(),salt);
                if(result.equals(mm)){
                    AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","新密码不能和以前的密码一致"),response);
                    return "success";
                }else{
                    one.set("mm",result);
                    Map map=new HashMap();
                    map.put("mm",result);
                    map.put("id",""+PK.get("id"));
                    //delegator.store(one);
                   return updateUser(map,response);
                }
            }
        }
        return "success";
    }
    /**
     * 通过map来更新User数据
     * */
    private static String updateUser(Map map,HttpServletResponse response) throws IOException, ServiceException, DocumentException {
        Map mapd=addMoidfyModeData(map);
        String code=""+mapd.get("returnnode");
        if("0".equals(code)) {
            AjaxUtil.writeJsonToResponse(map, response);
            return "success";
        } else AjaxUtil.writeJsonToResponse("{error:更新信息失败}",response);
        return "success";
    }
    /**获取一个用户数据*/
    public static String getUserOne(HttpServletRequest request,HttpServletResponse response) throws IOException, GenericEntityException {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));
        String entityName = (String) parameters.get("entityName");
        Map condition=(Map<String, Object>) parameters.get("PK");
        List<GenericValue> value=delegator.findList(entityName, EntityCondition.makeCondition(condition),null,null,null,false);
        GenericValue user=value.get(0);
        user.put("id",""+user.get("id"));
        AjaxUtil.writeJsonToResponse(user,response);
        return "success";
    }
    /**更新用户资料*/
    public static String updateUser(HttpServletRequest request,HttpServletResponse response) throws IOException, ServiceException, DocumentException {

        Map<String, Object> parameters = UtilHttp.getAttributeMap(request);
        Map<String, Object> fieldMap = (Map<String, Object>) parameters.get("fieldMap");
        return updateUser(fieldMap,response);
    }
    /**
     * 登陆用户
     * */
    public static String loginUser(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException, IOException {
        return UserEvents.ajaxLogin(request,response);
    }

    /**
     * 注册新用户流程
     * 1.验证手机短信验证码
     * 2.验证图形验证码
     * 3.往khgl表中增加一条新数据
     * 4.构造满足登录条件的参数，然后调用登录方法进行登录操作
     * */
    public static String registerUser(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException, IOException, ServiceException, DocumentException {
        if(!isCorrectCode(request,response,"phoneCode","sphoneCode","短信验证码填写不正确"))
            return "success";
        if(!isCorrectCode(request,response,"code","vcode","图形码填写不正确"))
            return "success";
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        GenericDelegator delegator = UtilGenerics.cast(parameters.get("delegator"));
        //查询推荐码开始
        String tjm= (String) parameters.get("tjm");
        String useTjm="";
        if(!("".equals(tjm)||tjm==null)){
            EntityCondition c1=EntityCondition.makeCondition("yqm", tjm);
            EntityCondition c2=EntityCondition.makeCondition("ygsj", tjm);
            List<EntityCondition> ls=new ArrayList<>();
            ls.add(c1);
            ls.add(c2);
            EntityCondition c3=EntityCondition.makeCondition(ls, EntityJoinOperator.OR);
            List<GenericValue> result=delegator.findList("UfYqmglView",c3,null,null,null,false);
            if(result.size()>0)
                useTjm=result.get(0).getString("ygid");
        }
        //查询推荐码结束
        Map condition=new HashMap();
        String phone= (String) parameters.get("phone");
        condition.put("sj",phone);
        if(phone.length()<6){
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","手机号码长度不正确"),response);
            return "success";
        }
        List<GenericValue> value=delegator.findList("UfKhglView", EntityCondition.makeCondition(condition),null,null,null,false);
        if(value.size()>0){
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error","手机号码已经注册"),response);
            return "success";
        }else{
            String password=phone.substring(phone.length()-6);
            request.setAttribute("userName",phone);
            request.setAttribute("passWord",password);
            /**增加新数据*
             * 构造xml字符串
             */
            Map map=new HashMap();
            map.put("sj",phone);
            map.put("mm",encrypt(password));
            map.put("xb","1");
            map.put("yjly", "53");
            if("".equals(useTjm)){
                map.put("cjr", "507");
                map.put("khjl", "507");
                map.put("kfjl", "507");
                map.put("ejly", "64");
            }else{
                map.put("cjr", useTjm);
                map.put("khjl", useTjm);
                map.put("kfjl", useTjm);
                map.put("ejly", "102");
            }
           Map ssm=addMoidfyModeData(map);
           String code= ""+ssm.get("returnnode");
            if("0".equals(code)) {
                //delegator.createOrStore(delegator.makeValue("UfKhgl",map));
                parameters = UtilHttp.getCombinedMap(request);
                //GenericValue u1 = UserEvents.getBaseValue(parameters, delegator, response);
                createRegisterMessage(delegator, ""+ssm.get("id"));
                String content = "尊敬的用户您好，您已注册成功，初始登录密码为手机后六位，请至个人中心修改密码并妥善保管。";
                SendMsg.sendPhoneCode(content, phone);
            }
            AjaxUtil.writeJsonToResponse(map,response);
            return "success";
        }
    }
    /**
     * 保存（新增、更新）
     * @throws RemoteException
     * @throws ServiceException
     * @throws MalformedURLException
     */

    public static Map addMoidfyModeData(Map<String,Object> map) throws RemoteException, ServiceException, MalformedURLException, DocumentException {
        ModeDateServicePortType modeDateServiceHttpPort = new ModeDateServiceLocator().getModeDateServiceHttpPort(new URL("http://101.132.38.102:8089/services/ModeDateService"));
        String modeid ="1";
        String dataid="";
        String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xml+="<ROOT>";
        xml+="<header>";
        xml+="<userid>507</userid>";//用户id
        xml+="<modeid>"+modeid+"</modeid>";//模块id
        Object userId=map.get("id");
        if(userId!=null)
            xml+="<id>"+userId+"</id>";//billid 如果是新增则值为空如果有数据则为修改
        else xml+="<id>"+dataid+"</id>";
        xml +="</header>";
        xml +="<search>";
        xml +="<condition />";
        xml +="<right>Y</right>";//是否验证权限
        xml +="</search>";
        xml +="<data id=\"\">";
        xml +="<maintable>";
        Iterator<String> it=map.keySet().iterator();
        StringBuilder builder=new StringBuilder();
        while(it.hasNext()) {
            String key = it.next();
            if(!"id".equals(key)) {
                Object value = map.get(key);
                if (value != null) {
                    String valueR = ""+ value;
                    builder = builder.append("<field>").append("<filedname>").append(key).append("</filedname>")
                            .append("<filedlabel></filedlabel>")
                            .append("<fileddbtype>");
                    if("xb".equals(key)){
                        builder=builder.append("int");
                    }else builder=builder.append("varchar(500)");
                   builder= builder.append("</fileddbtype>")
                            .append("<filedvalue>").append(valueR).append("</filedvalue>")
                            .append("<fieldshowname></fieldshowname>").append("</field>");
                }
            }
        }
        xml+=builder.toString();
        Date date=new Date();
        String d1=new SimpleDateFormat("yyyy-MM-dd").format(date);
        String ss4="<field><filedname>cjrq</filedname><filedlabel>创建日期</filedlabel><fileddbtype>varchar(500)</fileddbtype><filedvalue>"+d1+"</filedvalue><fieldshowname></fieldshowname></field>";
        xml+=ss4;
        xml +="</maintable>";
        xml +="<detail></detail>";
        xml +="</data>";
        xml +="</ROOT>";
        System.out.println("插入成功"+xml);
        Map maps= readStringXmlOut(modeDateServiceHttpPort.saveModeData(xml));
        return  maps;
    }

    public static Map readStringXmlOut(String xml) throws DocumentException {
        Map map = new HashMap();
        Document doc = null;
        // 将字符串转为XML
        doc = DocumentHelper.parseText(xml);
        // 获取根节点
        Element rootElt = doc.getRootElement();
        // 拿到根节点的名称
        System.out.println("根节点：" + rootElt.getName());

        // 获取根节点下的子节点head
        Iterator iter = rootElt.elementIterator("return");
        while (iter.hasNext()) {
            Element recordEle = (Element) iter.next();
            // 拿到head节点下的子节点title值
            String title = recordEle.elementTextTrim("id");
            map.put("id", title);
            String returnnode = recordEle.elementTextTrim("returnnode");
            map.put("returnnode", returnnode);
            String returnmessage = recordEle.elementTextTrim("returnmessage");
            map.put("returnmessage", returnmessage);
            System.out.println("title：" + title);
        }

        return map;
    }

    private static void createRegisterMessage(GenericDelegator delegator,String id) throws GenericEntityException {
        //往消息中增加一条新的记录
        GenericValue msg=delegator.makeValue("message");
        String mid=delegator.getNextSeqId("id");
        msg.set("id",mid);
        msg.set("sender","系统");
        String content="尊敬的用户您好，您已注册成功，初始登录密码为手机后六位，请至个人中心修改密码并妥善保管。";
        msg.set("content",content);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR,1);
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String arrTime=dateFormat.format(calendar.getTime());
        msg.set("offTime",""+arrTime);
        msg.set("gtime",dateFormat.format(new Date()));
        msg.set("type",id);
        delegator.create(msg);

    }
    /**验证手机短信码*/
    public static boolean isCorrectCode(HttpServletRequest request,HttpServletResponse response,String requestKey,String sessionKey,String errorMsg) throws IOException {
        System.out.println("验证时的sessionId"+request.getSession().getId());
        Object vcode=request.getSession().getAttribute(sessionKey);
        Object code=request.getAttribute(requestKey);
        if(vcode==null||code==null) {
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error", errorMsg), response);
            return false;
        }
        String scode=((String)code).toUpperCase();
        vcode=((String)vcode).toUpperCase();
        boolean result=vcode.equals(scode) ;
        if(!result){
            AjaxUtil.writeJsonToResponse(UtilMisc.toMap("error", errorMsg), response);
            return false;
        }
        return true;
    }
    /**密码加密*/
    private static String encrypt(String password){
        if(password==null||"".equals(password))
            return  null;
        String salt="$1$";
        char[] chars=new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','q','r','s','t','u','v','w','x','y','z'};
        int r=(int)Math.random()*25;
        salt+=chars[r];
        return Md5Crypt.md5Crypt(password.getBytes(),salt);
    }

    public static boolean isEmpty(String str){
        if(str==null||"".equals(str)){
            return true;
        }else return false;
    }
}
