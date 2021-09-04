package cn.itcast.wanxinp2p.uaa.agent;

import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.common.domain.CommonErrorCode;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "account-service",fallbackFactory = AccountFallbackFactory.class)
public interface AccountApiAgent {

    @PostMapping(value = "/account/l/accounts/session")
    RestResponse<AccountDTO> login(@RequestBody AccountLoginDTO accountLoginDTO);
}

@Component
@Slf4j
class AccountFallbackFactory implements FallbackFactory<AccountApiAgent>{

    @Override
    public AccountApiAgent create(Throwable cause) {
        return new AccountApiAgent() {
            @Override
            public RestResponse<AccountDTO> login(AccountLoginDTO accountLoginDTO) {
                log.info("fallback;reason was:",cause);
                return new RestResponse<>(CommonErrorCode.E_999995.getCode(),CommonErrorCode.E_999995.getDesc());
            }
        };
    }
}
