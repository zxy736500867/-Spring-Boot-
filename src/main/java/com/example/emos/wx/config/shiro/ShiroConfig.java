package com.example.emos.wx.config.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @Program: emos-wx-api
 * @Description: 过滤器和realm配置到Shiro框架中
 * @Author: 张鑫宇
 * @Create: 2022-01-06 14:46
 **/
@Configuration
public class ShiroConfig {

    /**
     * 用于封装Realm对象
     *
     * @param realm
     * @return
     */
    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    /**
     * 用于封装Filter对象,设置Filter拦截路径
     *
     * @param securityManager
     * @param filter
     * @return
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager, OAuth2Filter filter) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        //只能将filter转化为map的形式封装到shiroFilter
        HashMap<String, Filter> map = new HashMap<>();
        map.put("oauth2", filter);
        shiroFilter.setFilters(map);

        //设置filter路径，也是用map形式封装
        LinkedHashMap<String, String> filterPathMap = new LinkedHashMap<>();
        filterPathMap.put("/webjars/**", "anon");
        filterPathMap.put("/druid/**", "anon");
        filterPathMap.put("/app/**", "anon");
        filterPathMap.put("/sys/login", "anon");
        filterPathMap.put("/swagger-ui.html", "anon");
        filterPathMap.put("/v2/api-docs", "anon");
        filterPathMap.put("/swagger-ui.html", "anon");
        filterPathMap.put("/swagger-resources/**", "anon");
        filterPathMap.put("/captcha.jpg", "anon");
        filterPathMap.put("/user/register", "anon");
        filterPathMap.put("/user/login", "anon");
        filterPathMap.put("/test/**", "anon");
        filterPathMap.put("/**", "oauth2");
        shiroFilter.setFilterChainDefinitionMap(filterPathMap);
        return shiroFilter;
    }

    /**
     * 管理Shiro对象生命周期
     *
     * @return
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * AOP切面类,Web方法执行前，验证权限
     *
     * @param securityManager
     * @return
     */
    @Bean("authorizationAttributeSourceAdvisor")
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }


}