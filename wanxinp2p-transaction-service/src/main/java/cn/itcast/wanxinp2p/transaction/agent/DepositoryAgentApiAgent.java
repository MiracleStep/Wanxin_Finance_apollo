package cn.itcast.wanxinp2p.transaction.agent;

import cn.itcast.wanxinp2p.api.depository.model.LoanRequest;
import cn.itcast.wanxinp2p.api.transaction.model.ModifyProjectStatusDTO;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.api.transaction.model.UserAutoPreTransactionRequest;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "depository-agent-service")
public interface DepositoryAgentApiAgent {

    @PostMapping("/depository-agent/l/createProject")
    RestResponse<String> createProject(@RequestBody ProjectDTO projectDTO);

    @PostMapping("/depository-agent/l/user-auto-pre-transaction")
    RestResponse<String> userAutoPreTransaction(
            UserAutoPreTransactionRequest userAutoPreTransactionRequest);

    @PostMapping("/depository-agent/l/confirm-loan")
    RestResponse<String> confirmLoan(LoanRequest loanRequest);

    @PostMapping("/depository-agent/l/modify-project-status")
    RestResponse<String> modifyProjectStatus(ModifyProjectStatusDTO modifyProjectStatusDTO);

}
