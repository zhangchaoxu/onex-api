package com.nb6868.onex.api;

import com.nb6868.onex.common.config.WebSocketConfig;
import com.nb6868.onex.common.util.SpringBeanNameGenerator;
import com.nb6868.onex.common.wechat.WechatMpPropsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(nameGenerator = SpringBeanNameGenerator.class,
        // 扫描包
        basePackages = {"com.nb6868.onex.**"},
        // 排除指定类
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                WechatMpPropsConfig.class,
                WebSocketConfig.class
        })
)
@EnableAsync
public class PortalApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(PortalApiApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PortalApiApplication.class);
    }

}
