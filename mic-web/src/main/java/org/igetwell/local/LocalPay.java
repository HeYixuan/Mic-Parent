package org.igetwell.local;

import org.igetwell.common.enums.SignType;
import org.igetwell.common.utils.BeanUtils;
import org.igetwell.common.utils.HttpClientUtil;
import org.igetwell.common.utils.ParaMap;
import org.igetwell.common.utils.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class LocalPay {

    private static final Logger logger = LoggerFactory.getLogger(LocalPay.class);

    @Value("${defaultAppId}")
    private String defaultAppId;

    @Value("${paterKey}")
    private String paterKey;

    @Value("${mchId}")
    private String mchId;

    @Value("${attach}")
    private String attach;

    @Value("${domain}")
    private String domain;


    @Autowired
    private LocalSnowflakeService localSnowflakeService;



    /**
     * 预付款下单
     */
    public String preOrder(HttpServletRequest request,String productName, String productId, String fee) {
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        String timestamp = System.currentTimeMillis()/1000 + "";
        String tradeNo = attach + localSnowflakeService.nextId();
        String clientIp = IpKit.getIpAddr(request);

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

        try {
            return this.prePay(paraMap, SignType.MD5);
        } catch (Exception e) {
            logger.error("交易号：{} 预支付失败！", tradeNo, e);
        }
        return null;
    }

    /**
     * 预支付接口
     */
    public String prePay(Map<String, String> hashMap, SignType signType) throws Exception{
        hashMap.put("appId", defaultAppId);
        hashMap.put("mchId", mchId);

        // 创建请求参数
        Map<String, String> params = createParams(hashMap, signType);

        logger.info("=================统一下单调用开始====================");
        // 微信统一下单
        String result = pushOrder(params);

        Map<String, String> resultXml = BeanUtils.xmlBean2Map(result);

        String returnCode = resultXml.get("return_code");
        String resultCode = resultXml.get("result_code");
        String codeUrl = resultXml.get("code_url"); //扫码支付url

        boolean isSuccess = "SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode);
        if (!isSuccess) {
            throw new RuntimeException("统一支付失败！" +
                    "return_code:" + resultXml.get("return_code") + " " +
                    "return_msg:" + resultXml.get("return_msg"));
        }
        logger.info("统一下单调用结束！预支付交易标识：{}. {}", resultXml.get("prepay_id"), resultXml.get("return_msg"));
        return codeUrl;

    }


    private Map<String, String> createParams(Map<String, String> hashMap, SignType signType) throws Exception {

        Map<String, String> params = new HashMap<>();
        params.put("appid", hashMap.get("appId")); //  appId
        params.put("mch_id", hashMap.get("mchId")); //  商户号
        params.put("openid", hashMap.get("openId")); // trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识
        params.put("device_info", "WEB"); // 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
        params.put("nonce_str", hashMap.get("nonceStr")); // 随机字符串，不长于32位。
        params.put("body", hashMap.get("body")); // 商品或支付单简要描述
        params.put("attach", attach); // 附加数据=来源
        params.put("out_trade_no", hashMap.get("tradeNo")); // 商户系统内部的订单号,32个字符内、可包含字母
        params.put("fee_type", "CNY"); // 货币类型，默认人民币：CNY
        params.put("total_fee", hashMap.get("fee")); // 订单总金额，单位为分
        params.put("spbill_create_ip", hashMap.get("clientIp")); // APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
        params.put("trade_type", hashMap.get("tradeType")); // JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付
        params.put("notify_url", "http://" + domain + hashMap.get("notifyUrl")); // 通知地址，接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
        params.put("product_id", hashMap.get("productId"));

        String sign = SignUtils.createSign(params, paterKey, signType);
        params.put("sign", sign);

        return params;
    }

    private static String pushOrder(Map<String, String> params) {
        final String unifiedOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        return HttpClientUtil.getInstance().sendHttpPost(unifiedOrderUrl, BeanUtils.mapBean2Xml(params), "UTF-8");
    }
}
