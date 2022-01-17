package com.example.emos.wx.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Program: emos-wx-api
 * @Description: 权限验证JWT配置类
 * @Author: 张鑫宇
 * @Create: 2022-01-05 19:55
 **/
@Component
@Slf4j
public class JwtUtil {

    @Value("${emos.jwt.secret}")
    private String secret;

    @Value("${emos.jwt.expire}")
    private Integer expire;


    /**
     * 创建token
     *
     * @param userId
     * @return token
     */
    public String createToken(Integer userId) {
        //1.生成密钥
        Algorithm algorithm = Algorithm.HMAC256(secret);

        //2.生成过期时间
        DateTime offsetDay = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, 5);

        //3.创建令牌
        JWTCreator.Builder builder = JWT.create();

        //4.生成token
        String token = builder.withClaim("userId", userId).withExpiresAt(offsetDay).sign(algorithm);

        return token;
    }


    /**
     * 通过token逆向解码生成userId
     *
     * @param token
     * @return
     */
    public Integer getUserId(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Integer userId = jwt.getClaim("userId").asInt();
        return userId;
    }

    /**
     * 验证token是否正确，错误直接报异常，正确void
     *
     * @param token
     */
    public void verifierToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
    }


}