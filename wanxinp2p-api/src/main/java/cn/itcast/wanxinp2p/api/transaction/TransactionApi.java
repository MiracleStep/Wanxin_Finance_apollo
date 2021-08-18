package cn.itcast.wanxinp2p.api.transaction;

import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectQueryDTO;
import cn.itcast.wanxinp2p.common.domain.PageVO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

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

}