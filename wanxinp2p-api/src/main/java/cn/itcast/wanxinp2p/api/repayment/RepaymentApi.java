package cn.itcast.wanxinp2p.api.repayment;

import cn.itcast.wanxinp2p.api.repayment.model.ProjectWithTendersDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

public interface RepaymentApi {
    /**
     * 启动还款
     * @param projectWithTendersDTO
     * @return
     */
    public RestResponse<String> startRepayment(ProjectWithTendersDTO
                                                       projectWithTendersDTO);

}
