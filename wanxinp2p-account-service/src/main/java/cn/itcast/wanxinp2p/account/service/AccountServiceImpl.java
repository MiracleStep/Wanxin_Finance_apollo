package cn.itcast.wanxinp2p.account.service;

import cn.itcast.wanxinp2p.account.common.AccountErrorCode;
import cn.itcast.wanxinp2p.account.entity.Account;
import cn.itcast.wanxinp2p.account.mapper.AccountMapper;
import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.BusinessException;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private SmsService smsService;

    @Value("${sms.enable}")
    private boolean smsEnable;

    @Override
    public RestResponse getSMSCode(String mobile) {
        return smsService.getSMSCode(mobile);
    }

    @Override
    public Integer checkMobile(String mobile, String key, String code) {
        smsService.verifySmsCode(key,code);
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        int count = count(wrapper);
        return count>0?1:0;
    }

    @Override
    @Hmily(confirmMethod = "confirmRegister",cancelMethod = "cancelRegister")
    public AccountDTO register(AccountRegisterDTO registerDTO) {
        Account account = new Account();
        account.setUsername(registerDTO.getUsername());
        account.setMobile(registerDTO.getMobile());
        account.setPassword(PasswordUtil.generate(registerDTO.getPassword()));
        if (smsEnable) {
            account.setPassword(PasswordUtil.generate(account.getMobile()));
        }
        account.setDomain("c");


        save(account);
        return convertAccountEntityToDTO(account);
    }

    @Override
    public AccountDTO login(AccountLoginDTO accountLoginDTO) {
        //????????????????????????????????????1.??????????????????????????????????????????  2.?????????????????????????????????????????????
        Account account = null;
        if(accountLoginDTO.getDomain().equalsIgnoreCase("c")){
            //?????????c????????????????????????????????????
            account = getAccountByMobile(accountLoginDTO.getMobile());
        }else{
            //?????????b?????????????????????????????????
            account = getAccountByUserName(accountLoginDTO.getUsername());
        }
        if(account == null){
            //????????????????????????????????????????????????
            throw new BusinessException(AccountErrorCode.E_130104);
        }
        AccountDTO accountDTO = convertAccountEntityToDTO(account);
        if(smsEnable){
            //?????????true????????????????????????????????????????????????????????????
            return accountDTO;
        }

        //????????????
        boolean verify = PasswordUtil.verify(accountLoginDTO.getPassword(), account.getPassword());
        if(verify){
            return accountDTO;
        }
        throw new BusinessException(AccountErrorCode.E_130105);
    }

    public void confirmRegister(AccountRegisterDTO registerDTO) {
        log.info("execute confirmRegister");
    }


    public void cancelRegister(AccountRegisterDTO registerDTO) {
        log.info("execute cancelRegister");
        //????????????
        remove(Wrappers.<Account>lambdaQuery().eq(Account::getUsername,
                registerDTO.getUsername()));
    }


    /***
     * entity???dto??????
     * @param entity
     * @return
     */
    private AccountDTO convertAccountEntityToDTO(Account entity) {
        if (entity == null) {
            return null;
        }
        AccountDTO dto = new AccountDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private Account getAccountByMobile(String mobile){
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",mobile);
        Account one = getOne(queryWrapper);
        return one;
    }

    private Account getAccountByUserName(String username){
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        Account one = getOne(queryWrapper);
        return one;
    }

}
