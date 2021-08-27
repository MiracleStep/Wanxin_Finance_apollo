package cn.itcast.wanxinp2p.api.transaction.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <P>
 * 投标信息预览
 * </p>
 *
 * @author zhupeiyuan
 * @since 2019-06-25
 */
@Data
@ApiModel(value = "TenderOverviewDTO", description = "投标信息预览")
public class TenderOverviewDTO {

	@JsonSerialize(using= ToStringSerializer.class)
	@ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("投标人用户标识")
    private Long consumerId;

	@ApiModelProperty("投标人用户名")
	private String consumerUsername;

	@ApiModelProperty("投标冻结金额")
    private BigDecimal amount;

	@ApiModelProperty("投标方式")
    private String tenderWay = "手动出借";

	@ApiModelProperty("创建时间")
    private LocalDateTime createDate;

}
