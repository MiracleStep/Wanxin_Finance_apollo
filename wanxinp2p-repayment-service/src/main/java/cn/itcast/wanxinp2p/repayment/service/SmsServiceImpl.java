package cn.itcast.wanxinp2p.repayment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService{

    @Override
    /**
     * 由于未注册到腾讯云和阿里云短信服务，该接口将短信内容输出到控制台中。
     */
    public void sendRepaymentNotify(String mobile, String date, BigDecimal amount) {
        log.info("给手机号{},发送还款提醒：{} 金额：{}",mobile,date,amount);
    }
}
