package com.example.emos.wx.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Program: emos-wx-api
 * @Description: 把Token封装成认证对象
 * @Author: 张鑫宇
 * @Create: 2022-01-05 21:28
 **/
public class OAuth2Token implements AuthenticationToken {
    private String token;

    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}