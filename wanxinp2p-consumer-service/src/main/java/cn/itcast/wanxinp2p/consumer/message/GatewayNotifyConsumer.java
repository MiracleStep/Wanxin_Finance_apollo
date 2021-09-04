package cn.itcast.wanxinp2p.consumer.message;

import cn.itcast.wanxinp2p.api.depository.model.DepositoryConsumerResponse;
import cn.itcast.wanxinp2p.common.domain.BusinessException;
import cn.itcast.wanxinp2p.consumer.common.ConsumerErrorCode;
import cn.itcast.wanxinp2p.consumer.service.ConsumerService;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class GatewayNotifyConsumer {



    @Autowired
    private ConsumerService consumerService;

    public GatewayNotifyConsumer(@Value("${rocketmq.consumer.group}") String consumerGroup,
                                         @Value("${rocketmq.name-server}") String serverAddr) throws MQClientException{
        //绑定订阅组
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        //绑定ip地址
        defaultMQPushConsumer.setNamesrvAddr(serverAddr);
        //从最后获取消息
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //订阅主题和tag
        defaultMQPushConsumer.subscribe("TP_GATEWAY_NOTIFY_AGENT","*");

        //注册监听器
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    MessageExt message = msgs.get(0);
                    String topic = message.getTopic();
                    String tags = message.getTags();
                    String body = new String(message.getBody(), StandardCharsets.UTF_8);
                    if(tags.equals("PERSONAL_REGISTER")){
                        DepositoryConsumerResponse response = JSON.parseObject(body, DepositoryConsumerResponse.class);
                        consumerService.modifyResult(response);
                    }else if(tags.equals("RECHARGE")){
                        DepositoryConsumerResponse depositoryConsumerResponse = JSON.parseObject(body, DepositoryConsumerResponse.class);
                        Boolean result = consumerService.modifyRechargeStatus(depositoryConsumerResponse);
                        if(result == false){
                            throw new BusinessException(ConsumerErrorCode.E_140131);
                        }
                    }
                }catch (Exception e){
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQPushConsumer.start();
    }
}
