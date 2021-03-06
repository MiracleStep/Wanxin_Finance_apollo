package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.api.account.model.ConsumerRequest;
import cn.itcast.wanxinp2p.api.consumer.model.BankCardDTO;
import cn.itcast.wanxinp2p.api.consumer.model.BorrowerDTO;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerDTO;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerRegisterDTO;
import cn.itcast.wanxinp2p.api.depository.model.DepositoryConsumerResponse;
import cn.itcast.wanxinp2p.api.depository.model.GatewayRequest;
import cn.itcast.wanxinp2p.api.depository.model.RechargeRequest;
import cn.itcast.wanxinp2p.common.domain.*;
import cn.itcast.wanxinp2p.common.util.CodeNoUtil;
import cn.itcast.wanxinp2p.common.util.IDCardUtil;
import cn.itcast.wanxinp2p.consumer.agent.AccountApiAgent;
import cn.itcast.wanxinp2p.consumer.agent.DepositoryAgentApiAgent;
import cn.itcast.wanxinp2p.consumer.common.ConsumerErrorCode;
import cn.itcast.wanxinp2p.consumer.entity.BankCard;
import cn.itcast.wanxinp2p.consumer.entity.Consumer;
import cn.itcast.wanxinp2p.consumer.entity.RechargeRecord;
import cn.itcast.wanxinp2p.consumer.mapper.ConsumerMapper;
import cn.itcast.wanxinp2p.consumer.mapper.RechargeRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer> implements ConsumerService{

    @Autowired
    private AccountApiAgent accountApiAgent;

    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private DepositoryAgentApiAgent depositoryAgentApiAgent;

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public Integer checkMobile(String mobile) {
        return getByMobile(mobile)!=null?1:0;
    }



    @Override
    @Hmily(confirmMethod = "confirmRegister",cancelMethod = "cancelRegister")
    public void register(ConsumerRegisterDTO consumerRegisterDTO) {
        if(checkMobile(consumerRegisterDTO.getMobile())==1){
            //????????? ???????????????
            throw new BusinessException(ConsumerErrorCode.E_140105);
        }
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(consumerRegisterDTO,consumer);
        consumer.setUsername(CodeNoUtil.getNo(CodePrefixCode.CODE_NO_PREFIX));
        consumer.setUserNo(CodeNoUtil.getNo(CodePrefixCode.CODE_CONSUMER_PREFIX));
        consumer.setIsBindCard(0);
        save(consumer);

        //????????????account
        AccountRegisterDTO accountRegisterDTO = new AccountRegisterDTO();
        consumerRegisterDTO.setUsername(consumer.getUsername());
        BeanUtils.copyProperties(consumerRegisterDTO,accountRegisterDTO);
        RestResponse<AccountDTO> restResponse = accountApiAgent.register(accountRegisterDTO);
        if(restResponse.getCode()!= CommonErrorCode.SUCCESS.getCode()){
            throw new BusinessException(ConsumerErrorCode.E_140106);
        }

    }



    public void confirmRegister(ConsumerRegisterDTO consumerRegisterDTO) {
        log.info("execute confirmRegister");
    }
    public void cancelRegister(ConsumerRegisterDTO consumerRegisterDTO) {
        log.info("execute cancelRegister");
        remove(Wrappers.<Consumer>lambdaQuery().eq(Consumer::getMobile,
                consumerRegisterDTO.getMobile()));
    }

    public ConsumerDTO getByMobile(String mobile){
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",mobile);
        Consumer consumer = getOne(queryWrapper);
        return convertConsumerEntityToDTO(consumer);
    }

    @Override
    public BorrowerDTO getBorrower(Long id) {
        ConsumerDTO consumerDTO = get(id);
        BorrowerDTO borrowerDTO = new BorrowerDTO();
        BeanUtils.copyProperties(consumerDTO,borrowerDTO);
        Map<String, String> cardInfo = IDCardUtil.getInfo(borrowerDTO.getIdNumber());
        borrowerDTO.setAge(new Integer(cardInfo.get("age")));
        borrowerDTO.setBirthday(cardInfo.get("birthday"));
        borrowerDTO.setGender(cardInfo.get("gender"));
        return borrowerDTO;

    }

    @Override
    public BorrowerDTO getBorrowerByUserNo(String userNo) {
        ConsumerDTO consumerDTO = getByUserNo(userNo);
        BorrowerDTO borrowerDTO = new BorrowerDTO();
        BeanUtils.copyProperties(consumerDTO,borrowerDTO);
        Map<String, String> cardInfo = IDCardUtil.getInfo(borrowerDTO.getIdNumber());
        borrowerDTO.setAge(new Integer(cardInfo.get("age")));
        borrowerDTO.setBirthday(cardInfo.get("birthday"));
        borrowerDTO.setGender(cardInfo.get("gender"));
        return borrowerDTO;
    }




    private ConsumerDTO getByUserNo(String userNo) {
        Consumer entity = getOne(Wrappers.<Consumer>lambdaQuery().eq(Consumer::getUserNo, userNo));

        if (entity == null) {
            log.info("userNo???{}????????????????????????", userNo);
            throw new BusinessException(ConsumerErrorCode.E_140101);
        }
        return convertConsumerEntityToDTO(entity);
    }

    private ConsumerDTO get(Long id) {
        Consumer entity = getById(id);
        if (entity == null) {
            log.info("id???{}????????????????????????", id);
            throw new BusinessException(ConsumerErrorCode.E_140101);
        }
        return convertConsumerEntityToDTO(entity);
    }


    /**
     * entity??????dto
     * @param entity
     * @return
     */
    private ConsumerDTO convertConsumerEntityToDTO(Consumer entity) {
        if (entity == null) {
            return null;
        }
        ConsumerDTO dto = new ConsumerDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }


    @Override
    @Transactional
    public RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest) {
        //1.????????????????????????????????????
        ConsumerDTO consumerDTO = getByMobile(consumerRequest.getMobile());
        if(consumerDTO.getIsBindCard()==1){
            throw new BusinessException(ConsumerErrorCode.E_140105);
        }
        //2.???????????????????????????????????????????????????
        BankCardDTO bankCardDTO = bankCardService.getByCardNumber(consumerRequest.getCardNumber());
        if(bankCardDTO!=null && bankCardDTO.getStatus() == StatusCode.STATUS_IN.getCode()){
            throw new BusinessException(ConsumerErrorCode.E_140151);
        }
        consumerRequest.setId(consumerDTO.getId());
        //????????????????????????????????????
        consumerRequest.setUserNo(CodeNoUtil.getNo(CodePrefixCode.CODE_CONSUMER_PREFIX));
        consumerRequest.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        //??????????????????????????????????????????
        UpdateWrapper<Consumer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Consumer::getMobile, consumerDTO.getMobile());
        updateWrapper.lambda().set(Consumer::getUserNo, consumerRequest.getUserNo());
        updateWrapper.lambda().set(Consumer::getRequestNo, consumerRequest.getRequestNo());
        updateWrapper.lambda().set(Consumer::getFullname, consumerRequest.getFullname());
        updateWrapper.lambda().set(Consumer::getIdNumber, consumerRequest.getIdNumber());
        updateWrapper.lambda().set(Consumer::getAuthList, "ALL");

        //3.?????????????????????(consumer?????????)
        update(updateWrapper);

        //????????????????????????
        BankCard bankCard = new BankCard();
        bankCard.setConsumerId(consumerDTO.getId());
        bankCard.setBankCode(consumerRequest.getBankCode());
        bankCard.setCardNumber(consumerRequest.getCardNumber());
        bankCard.setMobile(consumerRequest.getMobile());
        bankCard.setStatus(StatusCode.STATUS_OUT.getCode());
        BankCardDTO existsBankCard = bankCardService.getByConsumerId(bankCard.getConsumerId());
        if(existsBankCard != null){
            bankCard.setId(existsBankCard.getId());
        }
        //4.?????????????????????
        bankCardService.saveOrUpdate(bankCard);

        //5.?????????????????????????????????????????????????????????????????????
        RestResponse<GatewayRequest> consumer = depositoryAgentApiAgent.createConsumer(consumerRequest);
        return consumer;
    }

    @Override
//    @Transactional
    public RestResponse<GatewayRequest> createRechargeRecord(String amount, String callbackUrl, ConsumerDTO consumerDTO, BigDecimal balance) {
        BigDecimal amountDecimal = new BigDecimal(amount);
//        if(amountDecimal.compareTo(balance)>0){
//            throw new BusinessException(ConsumerErrorCode.E_140131);
//        }
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setConsumerId(consumerDTO.getId());
        rechargeRecord.setUserNo(consumerDTO.getUserNo());
        rechargeRecord.setAmount(amountDecimal);
        rechargeRecord.setCreateDate(LocalDateTime.now());
        rechargeRecord.setCallbackStatus(StatusCode.STATUS_OUT.getCode());
        rechargeRecord.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        rechargeRecordMapper.insert(rechargeRecord);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setId(consumerDTO.getId());
        rechargeRequest.setRequestNo(rechargeRecord.getRequestNo());
        rechargeRequest.setUserNo(consumerDTO.getUserNo());
        rechargeRequest.setCallbackUrl(callbackUrl);
        rechargeRequest.setAmount(amountDecimal);
        RestResponse<GatewayRequest> rechargeRecordResponse = depositoryAgentApiAgent.createRechargeRecord(rechargeRequest);
        return rechargeRecordResponse;
    }

    @Override
    public Boolean modifyRechargeStatus(DepositoryConsumerResponse depositoryConsumerResponse) {
        int update = rechargeRecordMapper.update(null, Wrappers.<RechargeRecord>lambdaUpdate().eq(RechargeRecord::getRequestNo, depositoryConsumerResponse.getRequestNo()).set(RechargeRecord::getCallbackStatus, depositoryConsumerResponse.getStatus()));
        return update>0;
    }


    @Override
    @Transactional
    public Boolean modifyResult(DepositoryConsumerResponse response) {
        //1.????????????(??????)
        int status = response.getRespCode().equals(DepositoryReturnCode.RETURN_CODE_00000.getCode()) ? StatusCode.STATUS_IN.getCode() : StatusCode.STATUS_FAIL.getCode();

        //2.????????????
        Consumer consumer = getByRequestNo(response.getRequestNo());
        update(Wrappers.<Consumer>lambdaUpdate()
                .eq(Consumer::getId,consumer.getId())
                .set(Consumer::getIsBindCard,status)
                .set(Consumer::getStatus,status));

        //3.?????????????????????
        return bankCardService.update(Wrappers.<BankCard>lambdaUpdate()
                .eq(BankCard::getConsumerId, consumer.getId())
                .set(BankCard::getStatus, status).set(BankCard::getBankCode,
                        response.getBankCode())
                .set(BankCard::getBankName, response.getBankName()));
    }

    private Consumer getByRequestNo(String requestNo){
        return getOne(Wrappers.<Consumer>lambdaQuery().eq(Consumer::getRequestNo,requestNo));
    }


}
