package cn.itcast.wanxinp2p.account.service;

import cn.itcast.wanxinp2p.account.common.AccountErrorCode;
import cn.itcast.wanxinp2p.common.domain.BusinessException;
import cn.itcast.wanxinp2p.common.domain.CommonErrorCode;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.util.OkHttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${sms.url}")
    private String smsURL;

    @Value("${sms.enable}")
    private Boolean smsEnable;

    public RestResponse getSMSCode(String mobile) {
        if(smsEnable){
            String url = smsURL+"generate?effectiveTime=300&name=sms";
            String jsonString = "{\"mobile\":" + mobile + "}";
            return OkHttpUtil.post(url, jsonString);
        }
        return RestResponse.success();

    }

    /***
     * 校验验证码
     * @param key 校验标识 redis中的键
     * @param code 短信验证码
     */
    public void verifySmsCode(String key,String code){
       if(smsEnable){
           StringBuilder params = new StringBuilder("/verify?name=sms");
           params.append("&verificationKey=").append(key);
           params.append("&verificationCode=").append(code);
           RestResponse restResponse = OkHttpUtil.post(smsURL + params, "");
           if(restResponse.getCode()!= CommonErrorCode.SUCCESS.getCode()||restResponse.getResult().toString().equalsIgnoreCase("false")){
                throw new BusinessException(AccountErrorCode.E_140152);
           }
       }
    }


}
