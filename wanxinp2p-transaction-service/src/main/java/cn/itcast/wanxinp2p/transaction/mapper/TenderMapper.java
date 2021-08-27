package cn.itcast.wanxinp2p.transaction.mapper;

import cn.itcast.wanxinp2p.transaction.entity.Tender;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于操作投标信息的mapper接口
 */
@Mapper
public interface TenderMapper extends BaseMapper<Tender> {
    /**
     * 根据标的id, 获取标的已投金额, 如果未投返回0.0
     * @param id
     * @return
     */
    List<BigDecimal>  selectAmountInvestedByProjectId(Long id);
}
