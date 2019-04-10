package com.fadp.wx.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>自定义封装一层Http请求</p>
 * */
public class HttpUtil {

    /**
     * <p>发送get请求</p>
     * */
    public static String httpGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse execute = client.execute(httpGet);
        HttpEntity entity = execute.getEntity();
        return EntityUtils.toString(entity);
    }

    /**
     * <p>发送post请求</p>
     * @param url 请求的路径地址
     * @param list 传输的表单数据，具体格式是List<BasicNameValuePair>对象
     * */
    public static String httpPost(String url, List list) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(list,"utf-8"));
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse execute = client.execute(httpPost);
        HttpEntity entity = execute.getEntity();
        return EntityUtils.toString(entity);
    }

    /**
     * <p>发送post请求</p>
     * @param url 请求路径地址
     * @param map 发送的Json数据
     * */
    public static String httpPost(String url, Map map) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        ObjectMapper mapper=new ObjectMapper();
        httpPost.setEntity(new StringEntity(mapper.writeValueAsString(map), ContentType.create("text/json","utf-8")));
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse execute = client.execute(httpPost);
        HttpEntity entity = execute.getEntity();
        return EntityUtils.toString(entity);
    }
}
