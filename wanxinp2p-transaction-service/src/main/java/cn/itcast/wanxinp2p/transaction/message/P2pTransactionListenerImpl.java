package cn.itcast.wanxinp2p.transaction.message;

import cn.itcast.wanxinp2p.common.domain.ProjectCode;
import cn.itcast.wanxinp2p.transaction.entity.Project;
import cn.itcast.wanxinp2p.transaction.mapper.ProjectMapper;
import cn.itcast.wanxinp2p.transaction.service.ProjectService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RocketMQTransactionListener(txProducerGroup = "PID_START_REPAYMENT")
public class P2pTransactionListenerImpl implements RocketMQLocalTransactionListener {

    @Autowired
    private ProjectService projectService;

    @Resource
    private ProjectMapper projectMapper;

    /**
     * 执行本地事务
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        //1.解析消息
        JSONObject jsonObject = JSON.parseObject(new String((byte[]) msg.getPayload()));
        Project project = JSONObject.parseObject(jsonObject.getString("project"),Project.class);

        //2.执行本地事务
        Boolean result = projectService.updateProjectStatusAndStartRepayment(project);
        //3.返回执行结果
        if(result){
            return RocketMQLocalTransactionState.COMMIT;
        }else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }

    }

    /**
     * 执行事务回查
     * @param msg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        System.out.println("事务回查");
        //1. 解析消息
        JSONObject jsonObject = JSON.parseObject(new String((byte[]) msg.getPayload()));
        Project project = JSONObject.parseObject(jsonObject.getString("project"),Project.class);
        //2. 查询标的状态
        Project pro = projectMapper.selectById(project.getId());

        //3. 返回结果
        if(pro.getProjectStatus().equals(ProjectCode.REPAYING)){
            return RocketMQLocalTransactionState.COMMIT;
        }else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
