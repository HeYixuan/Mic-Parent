package org.igetwell.local;

import org.igetwell.common.enums.SignType;
import org.igetwell.common.utils.BeanUtils;
import org.igetwell.common.utils.HttpClientUtil;
import org.igetwell.common.utils.ParaMap;
import org.igetwell.common.utils.SignUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocalPay {

    @Value("defaultAppId")
    private String defaultAppId;

    @Value("paternerKey")
    private String paternerKey;

    @Value("mchId")
    private String mchId;

    @Value("attach")
    private String attach;

    @Value("domain")
    private String domain;


    @Autowired
    private LocalSnowflakeService localSnowflakeService;



    /**
     * 预付款下单
     */
    public void preOrder(HttpServletRequest request,String productName, String productId, String fee){
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        String timestamp = System.currentTimeMillis()/1000 + "";
        String tradeNo =attach+ localSnowflakeService.nextId();
        String clientIp = IpKit.getRealIpV2(request).split("\\,")[0].trim();

        Map<String,String> paraMap= ParaMap.create("productId", productId)
                .put("body", productName)
                .put("tradeNo", tradeNo)
                .put("fee", fee)
                .put("nonceStr", nonceStr)
                .put("timestamp",timestamp)
                .put("clientIp", clientIp)
                .put("tradeType", "NATIVE")
                .put("notifyUrl","/pay/payNotify")
                .getData();


    }

    /**
     * 预支付接口
     */
    public Map<String, String> prePay(Map<String, String> hashMap, SignType signType) throws Exception {
        hashMap.put("appId", defaultAppId);
        hashMap.put("mchId", mchId);
        hashMap.put("paternerKey", paternerKey);

        // 创建请求参数
        Map<String, String> params = createParams(hashMap, signType);

        // 微信统一下单
        String result = pushOrder(params);

        Map<String, String> resultXml = BeanUtils.xmlBean2Map(result);
        return resultXml;

    }


    private Map<String, String> createParams(Map<String, String> hashMap, SignType signType) throws Exception {

        Map<String, String> params = new HashMap<>();
        params.put("appid", hashMap.get("appId")); //  appId
        params.put("mch_id", hashMap.get("mchId")); //  商户号
        params.put("openid", hashMap.get("openid")); // trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识
        params.put("device_info", "WEB"); // 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
        params.put("nonce_str", hashMap.get("nonceStr")); // 随机字符串，不长于32位。
        params.put("body", hashMap.get("productName")); // 商品或支付单简要描述
        params.put("attach", attach); // 附加数据=来源
        params.put("out_trade_no", hashMap.get("tradeNo")); // 商户系统内部的订单号,32个字符内、可包含字母
        params.put("fee_type", "CNY"); // 货币类型，默认人民币：CNY
        params.put("total_fee", hashMap.get("fee")); // 订单总金额，单位为分
        params.put("spbill_create_ip", hashMap.get("clientIp")); // APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
        params.put("trade_type", hashMap.get("tradeType")); // JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付
        params.put("notify_url", "http://" + domain + hashMap.get("notifyUrl")); // 通知地址，接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
        params.put("product_id", hashMap.get("productId"));

        String sign = SignUtils.createSign(params, hashMap.get("paternerKey"), signType);
        params.put("sign", sign);

        return params;
    }

    private static String pushOrder(Map<String, String> params) {
        final String unifiedOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        return HttpClientUtil.getInstance().sendHttpPost(unifiedOrderUrl, BeanUtils.mapBean2Xml(params));
    }
}
