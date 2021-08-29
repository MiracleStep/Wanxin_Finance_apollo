package cn.itcast.wanxinp2p.repayment.service;

import java.math.BigDecimal;

public interface SmsService {
    /**
    * 发送还款短信通知
    * @param mobile 还款人手机号
    * @param date 日期
    * @param amount 应还金额
    */
    void sendRepaymentNotify(String mobile, String date, BigDecimal amount);


}