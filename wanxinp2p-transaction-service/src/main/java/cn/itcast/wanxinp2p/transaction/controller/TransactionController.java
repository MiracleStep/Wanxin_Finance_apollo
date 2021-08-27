package cn.itcast.wanxinp2p.transaction.controller;

import cn.itcast.wanxinp2p.api.transaction.TransactionApi;
import cn.itcast.wanxinp2p.api.transaction.model.*;
import cn.itcast.wanxinp2p.common.domain.PageVO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.transaction.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "交易中心服务", tags = "transaction")
@RestController
public class TransactionController implements TransactionApi {

    @Autowired
    private ProjectService projectService;

    @Override
    @ApiOperation("借款人发标")
    @ApiImplicitParam(name = "project", value = "标的信息", required = true,
    dataType = "Project", paramType = "body")
    @PostMapping("/my/projects")
    public RestResponse<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        ProjectDTO dto = projectService.createProject(projectDTO);
        return RestResponse.success(dto);
    }

    @Override
    @ApiOperation("检索标的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectQueryDTO", value = "标的信息查询对象",
                    required = true, dataType = "ProjectQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "order", value = "顺序", required = false,
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页码", required = true,
                    dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required =
                    true,
                    dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "sortBy", value = "排序字段", required = true,
                    dataType = "string", paramType = "query")})
    @PostMapping("/projects/q")
    public RestResponse<PageVO<ProjectDTO>> queryProjects(@RequestBody
                                                                  ProjectQueryDTO projectQueryDTO, String order, Integer pageNo, Integer pageSize, String sortBy) {
        PageVO<ProjectDTO> result = projectService.queryProjectsByQueryDTO(projectQueryDTO, order, pageNo, pageSize, sortBy);
        return RestResponse.success(result);
    }

    @Override
    @ApiOperation("管理员审核标的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "标的id", required = true,
                    dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "approveStatus", value = "审批状态",
                    required = true, dataType = "ref", paramType = "path")
    })
    @PutMapping("/m/projects/{id}/projectStatus/{approveStatus}")
    public RestResponse<String> projectsApprovalStatus(@PathVariable("id") Long id, @PathVariable("approveStatus") String approveStatus) {
        String result = projectService.projectsApprovalStatus(id,approveStatus);
        return RestResponse.success(result);
    }

    @Override
    @ApiOperation("从ES检索标的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectQueryDTO", value = "标的信息条件对象",
                    required = true, dataType = "ProjectQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "order", value = "顺序", required = false,
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页码", required = true,
                    dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required =
                    true,
                    dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "sortBy", value = "排序字段", required = false,
                    dataType = "string", paramType = "query")})
    @PostMapping("/projects/indexes/q")
    public RestResponse<PageVO<ProjectDTO>> queryProjects(@RequestBody
                                                                  ProjectQueryDTO projectQueryDTO, Integer pageNo, Integer pageSize, String
                                                                  sortBy, String order){
        return RestResponse.success(projectService.queryProjects(projectQueryDTO,order,pageNo,pageSize,sortBy));
    }

    @Override
    @ApiOperation("通过ids获取多个标的")
    @GetMapping("/projects/{ids}")
    public RestResponse<List<ProjectDTO>> queryProjectsIds(@PathVariable("ids") String ids) {
        List<ProjectDTO> projectDTOS = projectService.queryProjectsIds(ids);
        return RestResponse.success(projectDTOS);
    }

    @Override
    @ApiOperation("根据标的id查询投标记录")
    @GetMapping("/tenders/projects/{id}")
    public RestResponse<List<TenderOverviewDTO>> queryTendersByProjectId(@PathVariable("id") Long id) {
        List<TenderOverviewDTO> tenderOverviewDTOS = projectService.queryTendersByProjectId(id);
        return RestResponse.success(tenderOverviewDTOS);
    }

    @Override
    @ApiOperation("用户投标")
    @ApiImplicitParam(name = "projectInvestDTO", value = "投标信息",
            required = true, dataType = "ProjectInvestDTO", paramType = "body")
    @PostMapping("/my/tenders")
    public RestResponse<TenderDTO> createTender(@RequestBody ProjectInvestDTO projectInvestDTO) {
        TenderDTO tenderDTO = projectService.createTender(projectInvestDTO);
        return RestResponse.success(tenderDTO);
    }

    @Override
    @ApiOperation("审核标的满标放款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "标的id", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "approveStatus", value = "标的审核状态", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "commission", value = "平台佣金", required = true, dataType = "string", paramType = "query")
    })
    @PutMapping("/m/loans/{id}/projectStatus/{approveStatus}")
    public RestResponse<String> loansApprovalStatus(@PathVariable("id")Long id,@PathVariable("approveStatus") String approveStatus, String commission) {
        String s = projectService.loansApprovalStatus(id, approveStatus, commission);
        return RestResponse.success(s);
    }

}