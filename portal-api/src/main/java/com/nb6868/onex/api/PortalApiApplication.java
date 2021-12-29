package com.nb6868.onex.api;

import com.nb6868.onex.common.BaseApplication;
import com.nb6868.onex.common.util.SpringBeanNameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan(nameGenerator = SpringBeanNameGenerator.class, basePackages = {"com.nb6868.onex.**.dao", "com.nb6868.onex.api.**"})
@ComponentScan(nameGenerator = SpringBeanNameGenerator.class, basePackages = {"com.nb6868.onex.**"})
@Slf4j
public class PortalApiApplication extends BaseApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(PortalApiApplication.class, args);
        printEnvironmentInfo(app.getEnvironment());
    }

}
