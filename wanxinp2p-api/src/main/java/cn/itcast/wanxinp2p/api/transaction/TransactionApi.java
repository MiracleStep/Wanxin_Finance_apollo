package cn.itcast.wanxinp2p.api.transaction;

import cn.itcast.wanxinp2p.api.transaction.model.*;
import cn.itcast.wanxinp2p.common.domain.PageVO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

import java.util.List;
/**
* <P>
* 交易中心服务API
* </p>
*/
public interface TransactionApi {
    /**
    * 借款人发标
    * @param projectDTO
    * @return
    */
    RestResponse<ProjectDTO> createProject(ProjectDTO projectDTO);


    /**
     * 检索标的信息
     * @param projectQueryDTO 封装查询条件
     * @param order
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
    RestResponse<PageVO<ProjectDTO>> queryProjects(ProjectQueryDTO projectQueryDTO,
                                                   String order, Integer pageNo,
                                                   Integer pageSize, String sortBy);

    /**
     * 管理员审核标的信息
     *
     * @param id
     * @param approveStatus
     * @return
     */
    RestResponse<String> projectsApprovalStatus(Long id, String approveStatus);



    /**
     * 标的信息快速检索
     * @param projectQueryDTO
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @param order
     * @return
     */
    RestResponse<PageVO<ProjectDTO>> queryProjects(ProjectQueryDTO projectQueryDTO,
                                                   Integer pageNo, Integer pageSize, String sortBy,String order);

    /**
     * 通过ids获取多个标的
     * @param ids
     * @return
     */
    RestResponse<List<ProjectDTO>> queryProjectsIds(String ids);


    /**
     * 根据标的id查询投标记录
     * @param id
     * @return
     */
    RestResponse<List<TenderOverviewDTO>> queryTendersByProjectId(Long id);


    /**
     * 用户投标
     * @param projectInvestDTO
     * @return
     */
    RestResponse<TenderDTO> createTender(ProjectInvestDTO projectInvestDTO);


    /**
     * 审核标的满标放款
     *
     * @param id 标的id
     * @param approveStatus 审核状态
     * @param commission 平台佣金
     * @return
     */
    RestResponse<String> loansApprovalStatus(Long id, String approveStatus, String commission);
}