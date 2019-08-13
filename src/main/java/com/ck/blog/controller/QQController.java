package com.ck.blog.controller;

import com.alibaba.fastjson.JSONObject;
import com.ck.blog.exception.StateErrorException;
import com.ck.blog.utils.QQHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @author ck
 * @create 2019-05-18 20:32
 */
@Controller
public class QQController {


    @Value("${qq.oauth.http}")
    private String http;

    /**
     * 发起请求
     * @param session
     * @return
     */
    @GetMapping("/qq/oauth")
    public String qq(HttpSession session){
        //http://www.sansheng2019.cn/qq/callback 回调地址
        String backUrl = http + "/qq/callback";

        //用于第三方应用防止CSRF攻击
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        session.setAttribute("state",uuid);

        //Step1：获取Authorization Code
        String url = "https://graph.qq.com/oauth2.0/authorize?response_type=code"+
                "&client_id=" + QQHttpClient.APPID +
                "&redirect_uri=" + URLEncoder.encode(backUrl) +
                "&state=" + uuid;

        return "redirect:" + url;
    }

    /**
     * QQ回调
     * @param request
     * @return
     */
    @GetMapping("/qq/callback")
    public String qqcallback(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();

        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String uuid = (String) session.getAttribute("state");

        if(uuid != null){
            if(!uuid.equals(state)){
                throw new StateErrorException("QQ,state错误");
            }
        }


        //Step2：通过Authorization Code获取Access Token
        String backUrl = http + "/qq/callback";
        String url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code"+
                "&client_id=" + QQHttpClient.APPID +
                "&client_secret=" + QQHttpClient.APPKEY +
                "&code=" + code +
                "&redirect_uri=" + backUrl;

        String access_token = QQHttpClient.getAccessToken(url);

        //Step3: 获取回调后的 openid 值
        url = "https://graph.qq.com/oauth2.0/me?access_token=" + access_token;
        String openid = QQHttpClient.getOpenID(url);

        //Step4：获取QQ用户信息
        url = "https://graph.qq.com/user/get_user_info?access_token=" + access_token +
                "&oauth_consumer_key="+ QQHttpClient.APPID +
                "&openid=" + openid;

        JSONObject jsonObject = QQHttpClient.getUserInfo(url);

        //也可以放到Redis和mysql中
        session.setAttribute("openid",openid);
        session.setAttribute("nickname",(String)jsonObject.get("nickname"));
        session.setAttribute("figureurl_qq_2",(String)jsonObject.get("figureurl_qq_2"));


        return "redirect:/home";
    }

}
