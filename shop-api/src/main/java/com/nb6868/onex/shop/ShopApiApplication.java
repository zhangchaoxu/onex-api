package com.nb6868.onex.shop;

import com.nb6868.onex.common.config.WebSocketConfig;
import com.nb6868.onex.common.util.SpringBeanNameGenerator;
import com.nb6868.onex.common.wechat.WechatMpPropsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
@Slf4j
public class ShopApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws UnknownHostException  {
        ConfigurableApplicationContext app = SpringApplication.run(ShopApiApplication.class, args);
        Environment env = app.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "External: \thttp://{}:{}\n\t"+
                        "Doc: \thttp://{}:{}/doc.html\n"+
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ShopApiApplication.class);
    }

}
