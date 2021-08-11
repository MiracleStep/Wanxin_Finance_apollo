package cn.itcast.wanxinp2p.consumer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = MongoAutoConfiguration.class,scanBasePackages = {"org.dromara.hmily", "cn.itcast.wanxinp2p.consumer"})
@EnableDiscoveryClient
@EnableSwagger2
@EnableFeignClients(basePackages = {"cn.itcast.wanxinp2p.consumer.agent"})
public class ConsumerService {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerService.class, args);
    }
}