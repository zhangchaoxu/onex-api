package com.nb6868.onex.shop;

import com.nb6868.onex.common.util.SpringBeanNameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(nameGenerator = SpringBeanNameGenerator.class, basePackages = {"com.nb6868.onex.**"})
@Slf4j
public class ShopApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(ShopApiApplication.class, args);

        Environment env = app.getEnvironment();
        log.info("\n################## {} Running ##################\n" +
                        ":: Link ::\thttp://127.0.0.1:{}{}\n" +
                        ":: Doc ::\thttp://127.0.0.1:{}{}/doc.html\n" +
                        "################## {} Running ##################",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path"),
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path"),
                env.getProperty("spring.application.name"));
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ShopApiApplication.class);
    }

}
