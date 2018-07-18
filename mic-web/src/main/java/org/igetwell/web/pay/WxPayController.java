package org.igetwell.web.pay;

import org.igetwell.common.enums.JsApiType;
import org.igetwell.local.LocalPay;
import org.igetwell.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/WxPay")
public class WxPayController extends BaseController {

    @Autowired
    private LocalPay localPay;

    /**
     * 扫码支付
     * @return
     */
    @RequestMapping("/scanPay")
    @ResponseBody
    public String scanPay() {
        String codeUrl = localPay.scanOrder(request.get(), "官网费用","GW201807162055","10");
        return codeUrl;
    }


    /**
     * 公众号支付，APP内调起微信支付
     * 微信H5、APP内调起支付
     * @return
     */
    @RequestMapping("/preOrder")
    @ResponseBody
    public Map<String, String> preOrder() {
        return localPay.preOrder(request.get(), null, JsApiType.APP,"官网费用","GW201807162055","10");
    }


}
