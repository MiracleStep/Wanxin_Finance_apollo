package cn.itcast.wanxinp2p.depository.controller;

import cn.itcast.wanxinp2p.api.depository.model.DepositoryConsumerResponse;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.util.EncryptUtil;
import cn.itcast.wanxinp2p.depository.message.GatewayMessageProducer;
import cn.itcast.wanxinp2p.depository.service.DepositoryRecordService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(value = "银行存管系统通知服务", tags = "depository-agent")
@RestController
public class DepositoryNotifyController {

    @Autowired
    private DepositoryRecordService depositoryRecordService;

    @Autowired
    private GatewayMessageProducer gatewayMessageProducer;

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @ApiOperation("接受银行存管系统开户回调结果")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "serviceName", value = "请求的存管接口名", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "platformNo", value = "平台编号，平台与存管系统签约时获取", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "signature", value = "对reqData参数的签名", required = true, dataType = "String", paramType = "query"),
    @ApiImplicitParam(name = "reqData", value = "业务数据报文，json格式", required = true, dataType = "String", paramType = "query"),})
    @RequestMapping(value = "/gateway", method = RequestMethod.GET, params = "serviceName=PERSONAL_REGISTER")
    public String receiveDepositoryCreateConsumerResult(
            @RequestParam("serviceName") String serviceName,
            @RequestParam("platformNo") String platformNo,
            @RequestParam("signature") String signature,
            @RequestParam("reqData") String reqData) {
        //1.更新数据
        String jsonStr = EncryptUtil.decodeUTF8StringBase64(reqData);
        DepositoryConsumerResponse depositoryConsumerResponse = JSON.parseObject(jsonStr, DepositoryConsumerResponse.class);
        depositoryRecordService.modifyRequestStatus(depositoryConsumerResponse.getRequestNo(),depositoryConsumerResponse.getStatus());

        //2.给用户中心发送消息
        gatewayMessageProducer.personalRegister(depositoryConsumerResponse);

        //3.给银行存管系统返回结果
        return "OK";
    }

    @GetMapping("/test")
    public RestResponse testRocketMQ(){
        rocketMQTemplate.convertAndSend("TP_GATEWAY_NOTIFY_AGENT:PERSONAL_REGISTER", "response????");
        return RestResponse.success();
    }

    @ApiOperation("接受银行存管系统充值返回结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceName", value = "请求的银行存管接口名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "platformNo", value = "平台编号，平台与存管系统签约时获取", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "signature", value = "对reqData参数的签名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "reqData", value = "业务数据报文，json格式", required = true, dataType = "String", paramType = "query"),})
    @RequestMapping(value = "/gateway", method = RequestMethod.GET, params = "serviceName=RECHARGE")
    public String receiveDepositoryRechargeResult(@RequestParam("serviceName") String serviceName,
            @RequestParam("platformNo") String platformNo,
            @RequestParam("signature") String signature,
            @RequestParam("reqData") String reqData){
        String jsonStr = EncryptUtil.decodeUTF8StringBase64(reqData);
        DepositoryConsumerResponse depositoryConsumerResponse = JSON.parseObject(jsonStr, DepositoryConsumerResponse.class);
        //1.更新数据
        depositoryRecordService.modifyRequestStatus(depositoryConsumerResponse.getRequestNo(),depositoryConsumerResponse.getStatus());
        //2.给用户中心发送消息
        rocketMQTemplate.convertAndSend("TP_GATEWAY_NOTIFY_AGENT:RECHARGE", depositoryConsumerResponse);
        return "OK";
    }
}
