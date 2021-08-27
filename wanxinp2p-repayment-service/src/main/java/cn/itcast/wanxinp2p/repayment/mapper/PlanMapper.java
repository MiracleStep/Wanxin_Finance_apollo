package cn.itcast.wanxinp2p.repayment.mapper;

import cn.itcast.wanxinp2p.repayment.entity.RepaymentPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mapstruct.Mapper;
import java.util.List;
/**
 * <p>
 * 借款人还款计划 Mapper 接口
 * </p>
 */

@Mapper
public interface PlanMapper extends BaseMapper<RepaymentPlan> {
    List<RepaymentPlan> selectDueRepayment(String data);
}
