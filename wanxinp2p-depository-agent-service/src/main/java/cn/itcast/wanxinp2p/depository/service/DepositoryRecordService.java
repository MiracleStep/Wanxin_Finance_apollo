package cn.itcast.wanxinp2p.depository.service;

import cn.itcast.wanxinp2p.api.account.model.ConsumerRequest;
import cn.itcast.wanxinp2p.api.depository.model.DepositoryBaseResponse;
import cn.itcast.wanxinp2p.api.depository.model.DepositoryResponseDTO;
import cn.itcast.wanxinp2p.api.depository.model.GatewayRequest;
import cn.itcast.wanxinp2p.api.depository.model.LoanRequest;
import cn.itcast.wanxinp2p.api.transaction.model.ModifyProjectStatusDTO;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.api.transaction.model.UserAutoPreTransactionRequest;
import cn.itcast.wanxinp2p.depository.entity.DepositoryRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DepositoryRecordService extends IService<DepositoryRecord> {
    /**
     * 开通存管账户
     * @param consumerRequest 开户信息
     * @return
     */
    GatewayRequest createConsumer(ConsumerRequest consumerRequest);


    /**
     * 根据请求流水号更新请求状态
     * @param requestNo
     * @param requestsStatus
     * @return
     */
    Boolean modifyRequestStatus(String requestNo, Integer requestsStatus);

    /**
     * 保存标的
     * @param projectDTO
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> createProject(ProjectDTO projectDTO);


    /**
     * 投标预处理
     * @param userAutoPreTransactionRequest
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest);

    /**
     * 审核满标放款
     * @param loanRequest
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> confirmLoan(LoanRequest loanRequest);

    /**
     * 修改标的状态
     * @param modifyProjectStatusDTO
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse>
    modifyProjectStatus(ModifyProjectStatusDTO modifyProjectStatusDTO);


}
