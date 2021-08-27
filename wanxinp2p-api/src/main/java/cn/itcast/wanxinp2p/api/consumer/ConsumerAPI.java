package cn.itcast.wanxinp2p.api.consumer;

import cn.itcast.wanxinp2p.api.account.model.ConsumerRequest;
import cn.itcast.wanxinp2p.api.consumer.model.BalanceDetailsDTO;
import cn.itcast.wanxinp2p.api.consumer.model.BorrowerDTO;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerDTO;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerRegisterDTO;
import cn.itcast.wanxinp2p.api.depository.model.GatewayRequest;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

/**
 * 用户中心接口API
 */
public interface ConsumerAPI {

    /**
     * 用户注册  保存用户信息
     * @param consumerRegisterDTO
     * @return
     */
   RestResponse register(ConsumerRegisterDTO consumerRegisterDTO);


    /**
     * 生成开户请求数据
     * @param consumerRequest 开户信息
     * @return
     */
    RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest);

    /**
     * 获取当前登录用户
     * @return
     */
    RestResponse<ConsumerDTO> getCurrConsumer(String mobile);

    /**
     * 获取当前登录用户
     * @return
     */
    RestResponse<ConsumerDTO> getMyConsumer();

    /**
     * 获取借款人用户信息
     * @param id
     * @return
     */
    RestResponse<BorrowerDTO> getBorrower(Long id);

    /**
     * 获取当前登录用户余额信息
     * @param userNo 用户编码
     * @return
     * */
    RestResponse<BalanceDetailsDTO> getBalance(String userNo);


    /**
     * 获取当前登录用户余额信息,供前端使用
     * @return
     */
    RestResponse<BalanceDetailsDTO> getMyBalance();
}
