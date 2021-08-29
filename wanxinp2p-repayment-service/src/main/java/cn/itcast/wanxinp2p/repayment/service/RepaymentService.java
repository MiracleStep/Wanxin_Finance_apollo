package cn.itcast.wanxinp2p.repayment.service;

import cn.itcast.wanxinp2p.api.depository.model.RepaymentRequest;
import cn.itcast.wanxinp2p.api.repayment.model.ProjectWithTendersDTO;
import cn.itcast.wanxinp2p.repayment.entity.RepaymentDetail;
import cn.itcast.wanxinp2p.repayment.entity.RepaymentPlan;

import java.util.List;
public interface RepaymentService {

    /**
     * 启动还款
     * @param projectWithTendersDTO
     * @return
     */
    String startRepayment(ProjectWithTendersDTO projectWithTendersDTO);

    /**
     * 查询到期还款计划
     * @param date 格式为：yyyy-MM-dd
     * @return
     */
    List<RepaymentPlan> selectDueRepayment(String date);


    /**
     * 根据还款计划生成还款明细并保存
     * @param repaymentPlan
     * @return
     */
    RepaymentDetail saveRepaymentDetail(RepaymentPlan repaymentPlan);

    /**
     * 执行用户还款
     */
    void executeRepayment(String date);

    /**
     * 还款预处理：冻结借款人应还金额
     * @param repaymentPlan
     * @param preRequestNo
     * @return
     */
    Boolean preRepayment(RepaymentPlan repaymentPlan, String preRequestNo);


    /**
     * 确认还款处理
     * @param repaymentPlan
     * @param repaymentRequest
     * @return
     */
    Boolean confirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest);


    /**
     * 远程调用确认还款接口
     * @param repaymentPlan
     * @param repaymentRequest
     */
    void invokeConfirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest);

}
