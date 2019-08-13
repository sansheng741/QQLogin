package com.ck.blog.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author ck
 * @create 2019-05-18 20:32
 */
public class QQHttpClient {

    public static final String APPID = "101581466";

    public static final String APPKEY = "6169bc6e4ed0704cdff86f3a4bad4549";


    private static JSONObject parseJSONP(String jsonp){
        int startIndex = jsonp.indexOf("(");
        int endIndex = jsonp.lastIndexOf(")");

        String json = jsonp.substring(startIndex + 1,endIndex);

        return JSONObject.parseObject(json);
    }

    public static String getAccessToken(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        String token = null;

        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if(entity != null){
            String result = EntityUtils.toString(entity,"UTF-8");
            if(result.indexOf("access_token") >= 0){
                String[] array = result.split("&");
                for (String str : array){
                    if(str.indexOf("access_token") >= 0){
                        token = str.substring(str.indexOf("=") + 1);
                        break;
                    }
                }
            }
        }

        httpGet.releaseConnection();
        return token;
    }

    public static String getOpenID(String url) throws IOException {
        JSONObject jsonObject = null;
        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if(entity != null){
            String result = EntityUtils.toString(entity,"UTF-8");
            jsonObject = parseJSONP(result);
        }

        httpGet.releaseConnection();

        if(jsonObject != null){
            return jsonObject.getString("openid");
        }else {
            return null;
        }
    }

    public static JSONObject getUserInfo(String url) throws IOException {
        JSONObject jsonObject = null;
        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();


        if(entity != null){
            String result = EntityUtils.toString(entity,"UTF-8");
            jsonObject = JSONObject.parseObject(result);
        }

        httpGet.releaseConnection();

        return jsonObject;
    }
}
