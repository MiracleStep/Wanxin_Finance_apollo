package cn.itcast.wanxinp2p.transaction.service;

import cn.itcast.wanxinp2p.api.transaction.model.*;
import cn.itcast.wanxinp2p.common.domain.PageVO;
import cn.itcast.wanxinp2p.transaction.entity.Project;

import java.util.List;
/**
 * <P>
 * 交易中心service接口
 * </p>
 */
public interface ProjectService {
    /**
     * 创建标的
     *
     * @param project
     * @return ProjectDTO
     */
    ProjectDTO createProject(ProjectDTO project);

    /**
     * 根据分页条件检索标的信息
     *
     * @param projectQueryDTO
     * @param order
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
    PageVO<ProjectDTO> queryProjectsByQueryDTO(ProjectQueryDTO projectQueryDTO,
                                               String order, Integer pageNo, Integer pageSize, String
                                                       sortBy);

    /**
     * 管理员审核标的信息
     *
     * @param id
     * @param approveStatus
     * @return String
     */
    String projectsApprovalStatus(Long id, String approveStatus);


    /**
     * 交易中心标的查询
     * @param projectQueryDTO
     * @param order
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
    PageVO<ProjectDTO> queryProjects(ProjectQueryDTO projectQueryDTO, String order,
                                     Integer pageNo, Integer pageSize, String sortBy);


    /**
     * 通过ids获取多个标的
     * @param ids
     * @return
     */
    List<ProjectDTO> queryProjectsIds(String ids);


    /**
     * 根据标的id查询投标记录
     * @param id
     * @return
     */
    List<TenderOverviewDTO> queryTendersByProjectId(Long id);

    /****
     *用户投标
     */
    TenderDTO createTender(ProjectInvestDTO projectInvestDTO);

    /**
     * 审核标的满标放款
     * @param id
     * @param approveStatus
     * @param commission
     * @return String
     */
    String loansApprovalStatus(Long id, String approveStatus, String commission);


    /**
     * 修改标的状态为还款中
     * @param project
     * @return
     */
    public Boolean updateProjectStatusAndStartRepayment(Project project);
}
