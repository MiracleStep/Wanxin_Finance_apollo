package cn.itcast.wanxinp2p.repayment.mapper;

import cn.itcast.wanxinp2p.repayment.entity.RepaymentPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;

import java.util.List;
/**
 * <p>
 * 借款人还款计划 Mapper 接口
 * </p>
 */

@Mapper
public interface PlanMapper extends BaseMapper<RepaymentPlan> {

    @Select("SELECT * FROM repayment_plan WHERE DATE_FORMAT(SHOULD_REPAYMENT_DATE,'%Y-%m-%d') = #{date} AND REPAYMENT_STATUS = '0'")
    List<RepaymentPlan> selectDueRepayment(String data);

    /**
     查询所有到期的还款计划
     @return
     */
    @Select("SELECT * FROM repayment_plan WHERE DATE_FORMAT(SHOULD_REPAYMENT_DATE,'%Y-%m-%d') = #{date} AND REPAYMENT_STATUS ='0' AND MOD(number_of_periods,#{shardingCount}) = #{shardingItem}")
    List<RepaymentPlan> selectDueRepaymentList(@Param("date") String date, @Param("shardingCount") int shardingCount, @Param("shardingItem") int shardingItem);

}
