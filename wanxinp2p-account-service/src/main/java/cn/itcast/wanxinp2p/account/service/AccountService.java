package cn.itcast.wanxinp2p.account.service;

import cn.itcast.wanxinp2p.account.entity.Account;
import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AccountService extends IService<Account> {
    /**
     * 获取手机短信验证码
     * @param mobile 手机号
     */
    RestResponse getSMSCode(String mobile);

    /**
     * 检查手机号及验证码
     * @param mobile
     * @param key
     * @param code
     * @return
     */
    Integer checkMobile(String mobile,String key,String code);

    /**
     * 注册并保存用户信息
     * @param accountRegisterDTO
     * @return
     */
    AccountDTO register(AccountRegisterDTO accountRegisterDTO);

    /**
     * 用户登录
     * @param accountLoginDTO
     * @return
     */
    AccountDTO login(AccountLoginDTO accountLoginDTO);
}
