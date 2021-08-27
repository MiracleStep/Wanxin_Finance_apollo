package cn.itcast.wanxinp2p.transaction.service;

import cn.itcast.wanxinp2p.api.consumer.model.BalanceDetailsDTO;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerDTO;
import cn.itcast.wanxinp2p.api.depository.model.LoanDetailRequest;
import cn.itcast.wanxinp2p.api.depository.model.LoanRequest;
import cn.itcast.wanxinp2p.api.repayment.model.ProjectWithTendersDTO;
import cn.itcast.wanxinp2p.api.transaction.model.*;
import cn.itcast.wanxinp2p.common.domain.*;
import cn.itcast.wanxinp2p.common.util.CodeNoUtil;
import cn.itcast.wanxinp2p.common.util.CommonUtil;
import cn.itcast.wanxinp2p.transaction.agent.ConsumerApiAgent;
import cn.itcast.wanxinp2p.transaction.agent.ContentSearchApiAgent;
import cn.itcast.wanxinp2p.transaction.agent.DepositoryAgentApiAgent;
import cn.itcast.wanxinp2p.transaction.common.constant.TradingCode;
import cn.itcast.wanxinp2p.transaction.common.constant.TransactionErrorCode;
import cn.itcast.wanxinp2p.transaction.common.utils.IncomeCalcUtil;
import cn.itcast.wanxinp2p.transaction.common.utils.SecurityUtil;
import cn.itcast.wanxinp2p.transaction.entity.Project;
import cn.itcast.wanxinp2p.transaction.entity.Tender;
import cn.itcast.wanxinp2p.transaction.mapper.ProjectMapper;
import cn.itcast.wanxinp2p.transaction.mapper.TenderMapper;
import cn.itcast.wanxinp2p.transaction.message.P2pTransactionProducer;
import cn.itcast.wanxinp2p.transaction.model.LoginUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService{

    @Autowired
    private ConsumerApiAgent consumerApiAgent;

    @Autowired
    private ConfigService configService;

    @Autowired
    private DepositoryAgentApiAgent depositoryAgentApiAgent;

    @Autowired
    private ContentSearchApiAgent contentSearchApiAgent;

    @Autowired
    private TenderMapper tenderMapper;

    @Autowired
    private P2pTransactionProducer p2pTransactionProducer;

    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        RestResponse<ConsumerDTO> restResponse = consumerApiAgent.getCurrConsumer(SecurityUtil.getUser().getMobile());
        // 设置用户编码
        projectDTO.setUserNo(restResponse.getResult().getUserNo());
        // 设置用户id
        projectDTO.setConsumerId(restResponse.getResult().getId());
        // 生成标的编码
        projectDTO.setProjectNo(CodeNoUtil.getNo(CodePrefixCode.CODE_PROJECT_PREFIX));
        // 标的状态修改
        projectDTO.setProjectStatus(ProjectCode.COLLECTING.getCode());
        // 标的可用状态修改, 未同步
        projectDTO.setStatus(StatusCode.STATUS_OUT.getCode());
        // 设置标的创建时间
        projectDTO.setCreateDate(LocalDateTime.now());
        // 设置还款方式
        projectDTO.setRepaymentWay(RepaymentWayCode.FIXED_REPAYMENT.getCode());
        // 设置标的类型
        projectDTO.setType("NEW");

        Project project = convertProjectDTOToEntity(projectDTO);

        project.setBorrowerAnnualRate(configService.getBorrowerAnnualRate());
        project.setAnnualRate(configService.getAnnualRate());
        // 年化利率(平台佣金，利差)
        project.setCommissionAnnualRate(configService.getCommissionAnnualRate());
        //债权转让
        project.setIsAssignment(0);
        // 设置标的名字, 姓名+性别+第N次借款
        // 判断男女
        String sex = Integer.parseInt(restResponse.getResult().getIdNumber()
                .substring(16, 17)) % 2 == 0 ? "女士" : "先生";
        // 构造借款次数查询条件
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Project::getConsumerId, restResponse.getResult().getId());
        String name = restResponse.getResult().getFullname() + sex + "第" + (count(queryWrapper) + 1) + "次借款";
        project.setName(name);
        // 保存到数据库
        save(project);

        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        return projectDTO;
    }

    @Override
    public PageVO<ProjectDTO> queryProjectsByQueryDTO(ProjectQueryDTO projectQueryDTO, String order, Integer pageNo, Integer pageSize, String sortBy) {
        //带条件
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        // 标的类型
        if (StringUtils.isNotBlank(projectQueryDTO.getType())) {
            queryWrapper.lambda().eq(Project::getType, projectQueryDTO.getType());
        }
        // 起止年化利率(投资人) -- 区间
        if (null != projectQueryDTO.getStartAnnualRate()) {
            queryWrapper.lambda().ge(Project::getAnnualRate,
                    projectQueryDTO.getStartAnnualRate());
        }
        if (null != projectQueryDTO.getEndAnnualRate()) {
            queryWrapper.lambda().le(Project::getAnnualRate,
                    projectQueryDTO.getStartAnnualRate());
        }
        // 借款期限 -- 区间
        if (null != projectQueryDTO.getStartPeriod()) {
            queryWrapper.lambda().ge(Project::getPeriod,
                    projectQueryDTO.getStartPeriod());
        }
        if (null != projectQueryDTO.getEndPeriod()) {
            queryWrapper.lambda().le(Project::getPeriod,
                    projectQueryDTO.getEndPeriod());
        }
        // 标的状态
        if (StringUtils.isNotBlank(projectQueryDTO.getProjectStatus())) {
            queryWrapper.lambda().eq(Project::getProjectStatus,
                    projectQueryDTO.getProjectStatus());
        }

        //分页
        Page<Project> page = new Page<>(pageNo,pageSize);
        //排序
        if(StringUtils.isNotBlank(order)&&StringUtils.isNotBlank(sortBy)){
            if(order.toLowerCase().equals("asc")){
                queryWrapper.orderByAsc(sortBy);
            }else if(order.toLowerCase().equals("desc")){
                queryWrapper.orderByDesc(sortBy);
            }
        }else {
            queryWrapper.lambda().orderByDesc(Project::getCreateDate);
        }
        //执行查询
        IPage<Project> iPage = page(page, queryWrapper);
        List<Project> records = iPage.getRecords();
        //封装结果
        List<ProjectDTO> projectDTOS = convertProjectEntityListToDTOList(records);
        return new PageVO<>(projectDTOS, iPage.getTotal(), pageNo, pageSize);
    }

    @Override
    public String projectsApprovalStatus(Long id, String approveStatus) {
        //1.根据id查询标的信息并转换为DTO对象
        Project project = getById(id);
        ProjectDTO projectDTO = convertProjectEntityToDTO(project);
        //2.生成流水号
        if(StringUtils.isBlank(project.getRequestNo())){
            projectDTO.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
            boolean update = update(Wrappers.<Project>lambdaUpdate().set(Project::getRequestNo,
                    projectDTO.getRequestNo()).eq(Project::getId,id));

            System.out.println(update);
        }

        //3.通过feign远程访问存管代理服务，把标的信息传输过去
        RestResponse<String> restResponse = depositoryAgentApiAgent.createProject(projectDTO);
        if(DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(restResponse.getResult())){
            //4.根据结果修改状态
            update(Wrappers.<Project>lambdaUpdate().set(Project::getStatus,Integer.parseInt(approveStatus)).set(Project::getModifyDate,new Date()).eq(Project::getId,id));
            return "success";
        }
        //5. 如果失败就抛异常
        throw new BusinessException(TransactionErrorCode.E_150113);
    }

    private Project convertProjectDTOToEntity(ProjectDTO projectDTO) {
        if (projectDTO == null) {
            return null;
        }
        Project project = new Project();
        BeanUtils.copyProperties(projectDTO, project);
        return project;
    }

    private List<ProjectDTO> convertProjectEntityListToDTOList(List<Project>
                                                                       projectList) {
        if (projectList == null) {
            return null;
        }
        List<ProjectDTO> dtoList = new ArrayList<>();
        projectList.forEach(project -> {
            ProjectDTO projectDTO = new ProjectDTO();
            BeanUtils.copyProperties(project, projectDTO);
            dtoList.add(projectDTO);
        });
        return dtoList;
    }


    private ProjectDTO convertProjectEntityToDTO(Project project) {
        if (project == null) {
            return null;
        }
        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(project, projectDTO);
        return projectDTO;
    }



    @Override
    public PageVO<ProjectDTO> queryProjects(ProjectQueryDTO projectQueryDTO,
                                            String order, Integer pageNo, Integer pageSize, String sortBy) {
        RestResponse<PageVO<ProjectDTO>> esResponse =
                contentSearchApiAgent.queryProjectIndex(projectQueryDTO, pageNo, pageSize, sortBy, order);
        if (!esResponse.isSuccessful()) {
            throw new BusinessException(CommonErrorCode.UNKOWN);
        }
        return esResponse.getResult();
    }

    @Override
    public List<ProjectDTO> queryProjectsIds(String ids) {
        //1.查询标的的信息
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        List<Long> list = new ArrayList<>();
        Arrays.asList(ids.split(",")).forEach(str->{
            list.add(Long.parseLong(str));
        });
        queryWrapper.lambda().in(Project::getId,list);
        List<Project> projects = list(queryWrapper);
        List<ProjectDTO> dtos = new ArrayList<>();
        //2.转换为DTO对象
        for(Project project : projects){
            ProjectDTO projectDTO = convertProjectEntityToDTO(project);
            //3.获取剩余额度
            projectDTO.setRemainingAmount(getProjectRemainingAmount(project));
            //2.查询出借人数
            projectDTO.setTenderCount(tenderMapper.selectCount(Wrappers.<Tender>lambdaQuery().eq(Tender::getProjectId, project.getId())));
            dtos.add(projectDTO);
        }
        return dtos;
    }



    /**
     * 获取标的剩余可投额度
     * @param project
     * @return
     */
    private BigDecimal getProjectRemainingAmount(Project project) {
        // 根据标的id在投标表查询已投金额
        List<BigDecimal> decimalList =
                tenderMapper.selectAmountInvestedByProjectId(project.getId());
        // 求和结果集
        BigDecimal amountInvested = new BigDecimal("0.0");
        for (BigDecimal d : decimalList) {
            amountInvested = amountInvested.add(d);
        }
        // 得到剩余额度
        return project.getAmount().subtract(amountInvested);
    }

    @Override
    public List<TenderOverviewDTO> queryTendersByProjectId(Long id) {
        List<Tender> tenders = tenderMapper.selectList(Wrappers.<Tender>lambdaQuery().eq(Tender::getProjectId, id));
        List<TenderOverviewDTO> tenderOverviewDTOS = new ArrayList<>();
        tenders.forEach(tender->{
            TenderOverviewDTO tenderOverviewDTO = new TenderOverviewDTO();
            BeanUtils.copyProperties(tender,tenderOverviewDTO);
            tenderOverviewDTO.setConsumerUsername(CommonUtil.hiddenMobile(tenderOverviewDTO.getConsumerUsername()));
            tenderOverviewDTOS.add(tenderOverviewDTO);
        });
        return tenderOverviewDTOS;
    }

    @Override
    public TenderDTO createTender(ProjectInvestDTO projectInvestDTO) {
        //1.前置添加判断
        //1.1判断投标金额是否大于最小投标金额
        BigDecimal amount = new BigDecimal(projectInvestDTO.getAmount());
        BigDecimal miniInvestmentAmount = configService.getMiniInvestmentAmount();
        if(amount.compareTo(miniInvestmentAmount)<0){
            throw new BusinessException(TransactionErrorCode.E_150109);
        }
        //1.2判断用户账户余额是否足够
        LoginUser user = SecurityUtil.getUser();
        RestResponse<ConsumerDTO> restResponse = consumerApiAgent.getCurrConsumer(user.getMobile());
        ConsumerDTO result = restResponse.getResult();
        //获取用户余额
        RestResponse<BalanceDetailsDTO> balance = consumerApiAgent.getBalance(result.getUserNo());
        BigDecimal myBalance = balance.getResult().getBalance();
        if(myBalance.compareTo(amount)<0){
            throw new BusinessException(TransactionErrorCode.E_150112);
        }

        //1.3 判断标的是否满标,标的状态为FULL就表示满标
        Project project = getById(projectInvestDTO.getId());
        if(project.getProjectStatus().equalsIgnoreCase(ProjectCode.FULLY.getCode())){
            throw new BusinessException(TransactionErrorCode.E_150114);
        }
        //1.4判断投标金额是否超过剩余未投金额
        BigDecimal remainingAmount = getProjectRemainingAmount(project);//获取剩余未投金额
        if(amount.compareTo(remainingAmount)<1){
            //1.5 判断此次投标后的剩余未投金额是否满足最小投标金额
            //借款人需要借1万 现在已经投标了8千 还剩2千 本次投标1950
            BigDecimal subtract = remainingAmount.subtract(amount);
            if(subtract.compareTo(configService.getMiniInvestmentAmount())<0){
                if(subtract.compareTo(new BigDecimal("0.0"))!=0){
                    throw new BusinessException(TransactionErrorCode.E_150111);
                }
            }
        }else {
            throw new BusinessException(TransactionErrorCode.E_150110);
        }

        //2.保存投标信息并发送给存管代理服务
        //2.1保存投标信息
        //2.1 保存投标信息, 数据状态为: 未发布
        // 封装投标信息
        final Tender tender = new Tender();
        // 投资人投标金额( 投标冻结金额 )
        tender.setAmount(amount);
        // 投标人用户标识
        tender.setConsumerId(restResponse.getResult().getId());
        tender.setConsumerUsername(restResponse.getResult().getUsername());
        // 投标人用户编码
        tender.setUserNo(restResponse.getResult().getUserNo());
        // 标的标识
        tender.setProjectId(projectInvestDTO.getId());
        // 标的编码
        tender.setProjectNo(project.getProjectNo());
        // 投标状态
        tender.setTenderStatus(TradingCode.FROZEN.getCode());
        // 创建时间
        tender.setCreateDate(LocalDateTime.now());
        // 请求流水号
        tender.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        // 可用状态
        tender.setStatus(0);
        tender.setProjectName(project.getName());
        // 标的期限(单位:天)
        tender.setProjectPeriod(project.getPeriod());
        // 年化利率(投资人视图)
        tender.setProjectAnnualRate(project.getAnnualRate());
        // 保存到数据库
        tenderMapper.insert(tender);

        //2.2发送数据给存管代理服务
        // 构造请求数据
        UserAutoPreTransactionRequest userAutoPreTransactionRequest = new
                UserAutoPreTransactionRequest();
        // 冻结金额
        userAutoPreTransactionRequest.setAmount(amount);
        // 预处理业务类型
        userAutoPreTransactionRequest.setBizType(PreprocessBusinessTypeCode.TENDER.getCode());
        // 标的号
        userAutoPreTransactionRequest.setProjectNo(project.getProjectNo());
        // 请求流水号
        userAutoPreTransactionRequest.setRequestNo(tender.getRequestNo());
        // 投资人用户编码
        userAutoPreTransactionRequest.setUserNo(restResponse.getResult().getUserNo());
        // 设置 关联业务实体标识
        userAutoPreTransactionRequest.setId(tender.getId());
        // 远程调用存管代理服务
        RestResponse<String> response = depositoryAgentApiAgent.userAutoPreTransaction(userAutoPreTransactionRequest);
        //3.根据结果更新投标状态
        //3.1 判断结果
        if(response.getResult().equals(DepositoryReturnCode.RETURN_CODE_00000.getCode())){
            //3.2 修改状态为:已同步
            tender.setStatus(1);
            tenderMapper.updateById(tender);
            //3.3判断当前标的是否满标,如果满标要更新标的的状态
            BigDecimal remainAmount = getProjectRemainingAmount(project);
            if(remainAmount.compareTo(new BigDecimal(0))==0){
                project.setProjectStatus(ProjectCode.FULLY.getCode());
                updateById(project);
            }
            //3.4转换为DTO对象,并封装相关数据
            TenderDTO tenderDTO = convertTenderEntityToDTO(tender);
            //封装标的信息
            project.setRepaymentWay(RepaymentWayCode.FIXED_REPAYMENT.getDesc());
            tenderDTO.setProject(convertProjectEntityToDTO(project));
            //封装预期收益
            // 根据标的期限计算还款月数
            final Double ceil = Math.ceil(project.getPeriod() / 30.0);
            Integer month = ceil.intValue();
            tenderDTO.setExpectedIncome(IncomeCalcUtil.getIncomeTotalInterest(new BigDecimal(projectInvestDTO.getAmount()),configService.getAnnualRate(),month));
            return tenderDTO;
        }else {
            throw new BusinessException(TransactionErrorCode.E_150113);
        }
    }



    private TenderDTO convertTenderEntityToDTO(Tender tender) {
        if (tender == null) {
            return null;
        }
        TenderDTO tenderDTO = new TenderDTO();
        BeanUtils.copyProperties(tender, tenderDTO);
        return tenderDTO;
    }

    @Override
    public String loansApprovalStatus(Long id, String approveStatus, String commission) {
        //第一阶段：1.生成放款明细
        //标的信息
        Project project = getById(id);
        //投标信息
        QueryWrapper<Tender> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Tender::getProjectId, id);
        List<Tender> tenderList = tenderMapper.selectList(queryWrapper);
        //生成放款明细
        LoanRequest loanRequest = generateLoanRequest(project, tenderList, commission);
        //第二阶段：2.放款
        RestResponse<String> restResponse = depositoryAgentApiAgent.confirmLoan(loanRequest);
        if(restResponse.getResult().equals(DepositoryReturnCode.RETURN_CODE_00000.getCode())){
            updateTenderStatusAlreadyLoan(tenderList);
            //第三阶段：3.修改标的状态
            //创建请求参数对象
            ModifyProjectStatusDTO modifyProjectStatusDTO = new ModifyProjectStatusDTO();
            modifyProjectStatusDTO.setId(project.getId());
            modifyProjectStatusDTO.setProjectStatus(ProjectCode.REPAYING.getCode());
            modifyProjectStatusDTO.setRequestNo(loanRequest.getRequestNo());
            modifyProjectStatusDTO.setProjectNo(project.getProjectNo());
            //向存管代理服务发起请求
            RestResponse<String> modifyProjectStatus = depositoryAgentApiAgent.modifyProjectStatus(modifyProjectStatusDTO);
            if(modifyProjectStatus.getResult().equals(DepositoryReturnCode.RETURN_CODE_00000.getCode())){


                //第四阶段：4.启动还款
                //准备数据
                ProjectWithTendersDTO projectWithTendersDTO = new ProjectWithTendersDTO();
                //1.标的信息
                projectWithTendersDTO.setProject(convertProjectEntityToDTO(project));
                //2.投标信息
                List<TenderDTO> tenderDTOS = convertTenderEntityListToDTOList(tenderList);
                projectWithTendersDTO.setTenders(tenderDTOS);
                //3.投资人让利
                projectWithTendersDTO.setCommissionInvestorAnnualRate(configService.getCommissionAnnualRate());
                //4.借款人让利
                projectWithTendersDTO.setCommissionBorrowerAnnualRate(configService.getBorrowerAnnualRate());

                //涉及到分布式事务 通过RocketMQ
                p2pTransactionProducer.updateProjectStatusAndStartRepayment(project,projectWithTendersDTO);

                return "审核成功";
            }else {
                throw new BusinessException(TransactionErrorCode.E_150113);
            }
        }else {
            throw new BusinessException(TransactionErrorCode.E_150113);
        }

    }

    @Transactional(rollbackFor = BusinessException.class)
    @Override
    public Boolean updateProjectStatusAndStartRepayment(Project project) {
        //如果处理成功，就修改标的状态为还款中
        project.setProjectStatus(ProjectCode.REPAYING.getCode());
        return updateById(project);
    }

    /**
     * 修改投标信息的状态为已放款
     */
    private void updateTenderStatusAlreadyLoan(List<Tender> tenderList) {
        tenderList.forEach(tender -> {
            tender.setTenderStatus(TradingCode.LOAN.getCode());
            tenderMapper.updateById(tender);
        });
    }

    /**
     * 根据标的和投标信息生成放款明细
     * @param project
     * @param tenderList
     * @param commission
     * @return
     */
    public LoanRequest generateLoanRequest(Project project, List<Tender> tenderList, String commission){
        LoanRequest loanRequest = new LoanRequest();
        //封装标的id
        loanRequest.setId(project.getId());
        //封装平台佣金
        if(StringUtils.isNotBlank(commission)){
            loanRequest.setCommission(new BigDecimal(commission));
        }
        //封装标的编码
        loanRequest.setProjectNo(project.getProjectNo());

        //封装请求流水号
        loanRequest.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));

        List<LoanDetailRequest> details = new ArrayList<>();
        for (Tender tender : tenderList) {
            LoanDetailRequest detailRequest = new LoanDetailRequest();
            detailRequest.setAmount(tender.getAmount());
            detailRequest.setPreRequestNo(tender.getRequestNo());
            details.add(detailRequest);
        }
        loanRequest.setDetails(details);
        return loanRequest;
    }

    private List<TenderDTO> convertTenderEntityListToDTOList(List<Tender> records) {
        if (records == null) {
            return null;
        }
        List<TenderDTO> dtoList = new ArrayList<>();
        records.forEach(tender -> {
            TenderDTO tenderDTO = new TenderDTO();
            BeanUtils.copyProperties(tender, tenderDTO);
            dtoList.add(tenderDTO);
        });
        return dtoList;
    }


}
