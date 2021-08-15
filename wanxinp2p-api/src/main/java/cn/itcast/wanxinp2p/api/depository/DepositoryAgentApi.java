package cn.itcast.wanxinp2p.api.depository;

import cn.itcast.wanxinp2p.api.account.model.ConsumerRequest;
import cn.itcast.wanxinp2p.api.depository.model.GatewayRequest;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

/**
* 银行存管系统代理服务API
*/
public interface DepositoryAgentApi {
    /**
    * 开通存管账户
    * @param consumerRequest 开户信息
    * @return
    */
    RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest);
}