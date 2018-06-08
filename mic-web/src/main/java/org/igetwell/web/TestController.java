package org.igetwell.web;

import com.alibaba.fastjson.JSONObject;
import org.igetwell.common.utils.WeChatUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    @ResponseBody
    public void test(){
        JSONObject json = WeChatUtils.getAuthorizationCode();
        System.err.println(json);
    }
}
