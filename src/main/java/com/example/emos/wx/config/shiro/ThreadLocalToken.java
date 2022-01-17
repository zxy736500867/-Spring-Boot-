package com.example.emos.wx.config.shiro;


import org.springframework.stereotype.Component;

/**
 * @Program: emos-wx-api
 * @Description: 该类是用于在过滤器和AOP之间传递Token
 * @Author: 张鑫宇
 * @Create: 2022-01-06 11:29
 **/
@Component
public class ThreadLocalToken {
    private ThreadLocal<String> local = new ThreadLocal<>();

    public void setToken(String token) {
        local.set(token);
    }

    public String getToken() {
        return local.get();
    }

    public void clear() {
        local.remove();
    }

}