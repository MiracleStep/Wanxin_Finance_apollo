package cn.itcast.wanxinp2p.api.repayment;

import cn.itcast.wanxinp2p.api.repayment.model.ProjectWithTendersDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import org.springframework.web.bind.annotation.PathVariable;

public interface RepaymentApi {
    /**
     * 启动还款
     * @param projectWithTendersDTO
     * @return
     */
    public RestResponse<String> startRepayment(ProjectWithTendersDTO
                                                       projectWithTendersDTO);

    /**
     * 执行用户还款
     * @param date 日期
     */
    public void testExecuteRepayment(@PathVariable String date);



}
