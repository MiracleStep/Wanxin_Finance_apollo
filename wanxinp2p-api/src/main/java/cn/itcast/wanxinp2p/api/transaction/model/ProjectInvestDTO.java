package cn.itcast.wanxinp2p.api.transaction.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <P>
 * ??????
 * </p>
 */

@Data
@ApiModel(value = "ProjectInvestDTO", description = "?????????")
public class ProjectInvestDTO {

    @ApiModelProperty("?????")
    private Long id;

    @ApiModelProperty("?????")
    private String amount;
}
