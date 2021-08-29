package cn.itcast.wanxinp2p.repayment.job;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticJobConfig {

    @Autowired
    RepaymentJob repaymentJob;
    @Autowired
    ZookeeperRegistryCenter registryCenter;

    @Value("${p2p.job.count}")
    private int shardingCount;

    @Value("${p2p.job.cron}")
    private String cron;

    /**
     * 配置任务详细信息
     * @param jobClass 任务执行类
     * @param cron 执行策略
     * @param shardingTotalCount 分片数量
     * @return
     */
    private LiteJobConfiguration createJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                        final String cron, final int shardingTotalCount){
        //创建JobCoreConfigurationBuilder
        JobCoreConfiguration.Builder JobCoreConfigurationBuilder =
                JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount);
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfigurationBuilder.build();
        //创建SimpleJobConfiguration
        SimpleJobConfiguration simpleJobConfiguration = new
                SimpleJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName());
        //创建LiteJobConfiguration
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build();
        return liteJobConfiguration;
    }


    @Bean(initMethod = "init")
    public SpringJobScheduler initSimpleElasticJob() {
        //创建SpringJobScheduler
        SpringJobScheduler springJobScheduler = new
                SpringJobScheduler(repaymentJob, registryCenter, createJobConfiguration(repaymentJob.getClass(), cron, shardingCount));
        return springJobScheduler;
    }
}
