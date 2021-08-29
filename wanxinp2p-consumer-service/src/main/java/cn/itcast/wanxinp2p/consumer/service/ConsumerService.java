package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.api.account.model.ConsumerRequest;
import cn.itcast.wanxinp2p.api.consumer.model.BorrowerDTO;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerDTO;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerRegisterDTO;
import cn.itcast.wanxinp2p.api.depository.model.DepositoryConsumerResponse;
import cn.itcast.wanxinp2p.api.depository.model.GatewayRequest;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.consumer.entity.Consumer;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ConsumerService extends IService<Consumer> {
    /**
     * 检测用户是否存在
     * @param mobile
     * @return
     */
    Integer checkMobile(String mobile);
    /**
     * 用户注册
     * @param consumerRegisterDTO
     * @return
     */
    void register(ConsumerRegisterDTO consumerRegisterDTO);

    /**
     生成开户数据
     @param consumerRequest
     @return
     */
    RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest);


    /**
     * 更新开户结果
     * @param response
     * @return
     */
    Boolean modifyResult(DepositoryConsumerResponse response);

    /**
     * 根据手机号获取当前用户的信息
     * @param mobile
     * @return
     */
    ConsumerDTO getByMobile(String mobile);

    /**
     * 获取借款人基本信息
     * @param id
     * @return
     */
    BorrowerDTO getBorrower(Long id);

    /**
     * 获取借款人基本信息
     * @param id
     * @return
     */
    BorrowerDTO getBorrowerByUserNo(String userNo);
}
