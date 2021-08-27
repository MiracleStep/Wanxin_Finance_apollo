package cn.itcast.wanxinp2p.transaction.agent;

import cn.itcast.wanxinp2p.api.consumer.model.BalanceDetailsDTO;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "consumer-service")
public interface ConsumerApiAgent {

    @GetMapping("/consumer/l/currConsumer/{mobile}")
    public RestResponse<ConsumerDTO> getCurrConsumer(@PathVariable("mobile") String mobile);


    @GetMapping("/consumer/l/balances/{userNo}")
    public RestResponse<BalanceDetailsDTO> getBalance(@PathVariable String userNo);
}
