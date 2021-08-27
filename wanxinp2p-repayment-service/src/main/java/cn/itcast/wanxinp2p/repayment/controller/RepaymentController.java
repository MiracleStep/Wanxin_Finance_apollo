package cn.itcast.wanxinp2p.repayment.controller;

import cn.itcast.wanxinp2p.api.repayment.RepaymentApi;
import cn.itcast.wanxinp2p.api.repayment.model.ProjectWithTendersDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.repayment.service.RepaymentService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 还款微服务的Controller
 */
@RestController
public class RepaymentController implements RepaymentApi {

    @Autowired
    private RepaymentService repaymentService;

    @Override
    @ApiOperation("启动还款")
    @ApiImplicitParam(name = "projectWithTendersDTO", value = "通过id获取标的信息",
            required = true, dataType = "ProjectWithTendersDTO",
            paramType = "body")
    @PostMapping("/l/start-repayment")
    public RestResponse<String> startRepayment(@RequestBody ProjectWithTendersDTO projectWithTendersDTO) {
        String result = repaymentService.startRepayment(projectWithTendersDTO);
        return RestResponse.success(result);
    }
}
