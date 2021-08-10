package cn.itcast.wanxinp2p.api.account;

import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

/**
 * 统一账号服务接口API
 */
public interface AccountAPI {
    /**
     * 获取短信验证码
     * @param mobile
     * @return
     */
    RestResponse getSMSCode(String mobile);

    /**
     * 校验手机号和验证码
     * @param key 用户校验标识
     * @param code 验证码
     */
    RestResponse<Integer> checkMobile(String mobile,String key,String code);


    /***
     * 注册 保存用户信息
     * @param accountRegisterDTO
     * @return
     */
    RestResponse<AccountDTO> register(AccountRegisterDTO accountRegisterDTO);

    /**
     * 用户登录
     * @param accountLoginDTO
     * @return
     */
    RestResponse<AccountDTO> login(AccountLoginDTO accountLoginDTO);
}
