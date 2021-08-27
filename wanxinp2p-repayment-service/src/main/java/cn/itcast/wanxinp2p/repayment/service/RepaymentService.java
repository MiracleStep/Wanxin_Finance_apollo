package cn.itcast.wanxinp2p.repayment.service;

import cn.itcast.wanxinp2p.api.repayment.model.ProjectWithTendersDTO;

public interface RepaymentService {

    /**
     * 启动还款
     * @param projectWithTendersDTO
     * @return
     */
    String startRepayment(ProjectWithTendersDTO projectWithTendersDTO);

}
