package com.nb6868.onex.portal.config;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.nb6868.onex.common.config.BaseSwaggerConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.List;

/**
 * Swagger配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig extends BaseSwaggerConfig {

    protected final OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * 注意需要knife4j.enable: true
     * 否则会提示注入失败
     */
    @Autowired
    public SwaggerConfig(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }

    protected List<VendorExtension> getExtensions() {
        return openApiExtensionResolver.buildSettingExtensions();
    }

    @Bean("crm")
    public Docket createCrmApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("crm")
                .select()
                // 包下的类,生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.portal.modules.crm.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean("default")
    public Docket createDefaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("default")
                .select()
                // 扫描注解,生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 包下的类,生成接口文档
                //.apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.modules.*.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

}
