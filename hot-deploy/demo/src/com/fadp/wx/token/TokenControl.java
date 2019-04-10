package com.fadp.wx.token;

import com.fadp.wx.redis.JedisPoolConnection;
import com.fadp.wx.util.HttpUtil;
import com.hanlin.fadp.common.AjaxUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * <p>token控制类，用于获取Token和refresToken</p>
 * */
public class TokenControl {

    static final  String appId="";

    static final String appSecret="";

    static final  String codeUrl="https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";

    static final String tokenUrl="https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    static final String refreshUrl="https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";

    /**
     * <p>获取code</p>
     * */
    public static String getCode() throws IOException {
        String url=String.format(codeUrl,appId);
        String result=HttpUtil.httpGet(url);
        System.out.println("结果是："+result);
        return result;
    }

    /**
     * <p>获取openId</p>
     * */
    public static String getOpenId() throws IOException {
        String code=getCode();
        String url=String.format(tokenUrl,appId,appSecret,code);
        String result=HttpUtil.httpGet(url);
        String openId=getValue(result,"openid");
        return openId;
    }
    /**
     * <p>获取返回的结果中的JSON对象，对应Key的Value值</p>
     * @param result json字符串对象
     * @param key 键值
     * @return key对应的value值，如果不存在返回为null
     * */
    private static String getValue(String result,String key){
        JSONObject os=JSONObject.fromObject(result);
        String rs=os.getString(key);
        if(rs!=null)
            return rs;
        else {
            System.out.println(result);
            return null;
        }
    }
}
