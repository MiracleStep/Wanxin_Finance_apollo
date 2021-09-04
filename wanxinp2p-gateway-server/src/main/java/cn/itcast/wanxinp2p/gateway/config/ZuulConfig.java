package cn.itcast.wanxinp2p.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulErrorFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPostFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPreFilter;
import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.netflix.zuul.ZuulFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;
import java.util.Set;

@Configuration
public class ZuulConfig {

    @Bean
    public FilterRegistrationBean corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(18000L);
        source.registerCorsConfiguration("/**", config);
        CorsFilter corsFilter = new CorsFilter(source);
        FilterRegistrationBean bean = new FilterRegistrationBean(corsFilter);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean //pre过滤器，在请求路由之前进行限流操作
    public ZuulFilter sentinelZuulPreFilter() {
        return new SentinelZuulPreFilter();
    }
    @Bean //post过滤器，路由之后恢复资源
    public ZuulFilter sentinelZuulPostFilter() {
        return new SentinelZuulPostFilter();
    }
    @Bean //error过滤器，异常后的处理
    public ZuulFilter sentinelZuulErrorFilter() {
        return new SentinelZuulErrorFilter();
    }

    /**
    * 从Apollo上获取限流规则并进行配置
    */
    @PostConstruct
    private void initGatewayRules() {
        String namespaceName = "application"; // 对应Apollo的命名空间名称
        String ruleKey = "sentinel.rule.gateway-flow-rule"; //限流规则的key
        String defaultRules = "";

        //从Apollo上获取限流规则并封装到ApolloDataSource
        ApolloDataSource gatewayRuleDataSource = new ApolloDataSource<>(namespaceName,ruleKey,defaultRules,source->
                        JSON.parseObject(source,new TypeReference<Set<GatewayFlowRule>>() {}));
        //注册限流规则使其生效
        GatewayRuleManager.register2Property(gatewayRuleDataSource.getProperty());

    }




    }
