package com.example.emos.wx.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @Program: emos-wx-api
 * @Description: Swagger配置类
 * @Author: 张鑫宇
 * @Create: 2022-01-05 11:02
 **/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * @return
     */
    @Bean
    public Docket creatRestApi() {
        //1.创建容器
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        ApiInfoBuilder builder = new ApiInfoBuilder();
        //2.设置Swagger标题
        builder.title("EMOS在线协同办公系统");
        //3.将相关配置加入到容器中
        ApiInfo info = builder.build();
        docket.apiInfo(info);

        //4.将controller层中的api放到Swagger中，便于测试
        ApiSelectorBuilder selectorBuilder = docket.select();
        //5.先将项目路径下所有包加入到Swagger中
        selectorBuilder.paths(PathSelectors.any());
        //6.只有在方法上面加入@ApiOperation注解才能生效
        selectorBuilder.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
        //7.重写加入到容器中
        docket = selectorBuilder.build();

        //8.将JWT加入到Swagger中
        ApiKey apiKey = new ApiKey("token", "token", "header");
        ArrayList<ApiKey> apiKeyArrayList = new ArrayList<>();
        apiKeyArrayList.add(apiKey);
        docket.securitySchemes(apiKeyArrayList);

        //9.设置令牌的作用域
        AuthorizationScope scope = new AuthorizationScope("global", "设置全局作用域");
        //10.下面都是将其封装到容器方法中，有一说一Swagger2整合的真垃圾
        AuthorizationScope[] scopes = {scope};
        SecurityReference reference = new SecurityReference("token", scopes);
        List refList = new ArrayList<>();
        refList.add(reference);
        SecurityContext context = SecurityContext.builder().securityReferences(refList).build();
        List cxtList = new ArrayList<>();
        cxtList.add(context);
        docket.securityContexts(cxtList);


        return docket;
    }


}