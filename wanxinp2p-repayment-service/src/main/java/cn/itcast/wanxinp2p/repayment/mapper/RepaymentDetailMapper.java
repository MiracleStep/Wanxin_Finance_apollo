package cn.itcast.wanxinp2p.repayment.mapper;

import cn.itcast.wanxinp2p.repayment.entity.RepaymentDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mapstruct.Mapper;

/**
 * 操作repayment_detail表的mapper接口
 */
@Mapper
public interface RepaymentDetailMapper extends BaseMapper<RepaymentDetail> {
}
