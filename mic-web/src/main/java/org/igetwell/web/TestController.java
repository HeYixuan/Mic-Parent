package org.igetwell.web;

import org.igetwell.common.utils.WeChatUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class TestController {


    @RequestMapping("/getAuthLogin")
    public void testAuth(HttpServletResponse response){
        String redirect_uri = "http://group.360yeke.com/msg";
        try {
            String url = WeChatUtils.getAuthorizeUrl(redirect_uri);
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("微信授权登录异常.{}",e);
        }

    }

    @RequestMapping("/getAccessToken")
    public void getAccessToken(String code){
        try {
            WeChatUtils.getAccessToken();
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("获取微信授权登录AccessToken异常.{}",e);
        }

    }
}
