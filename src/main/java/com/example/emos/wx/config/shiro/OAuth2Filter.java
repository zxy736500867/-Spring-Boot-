package com.example.emos.wx.config.shiro;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Program: emos-wx-api
 * @Description: 创建过滤器
 * @Author: 张鑫宇
 * @Create: 2022-01-06 11:51
 **/
@Component
@Scope("prototype")
@Slf4j
public class OAuth2Filter extends AuthenticatingFilter {

    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Value("${emos.jwt.expire}")
    private Integer cacheExpire;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 从请求中获取token字符串
     *
     * @param request
     * @return token
     */
    private String getRequestToken(HttpServletRequest request) {
        //1.从请求头获取token
        String token = request.getHeader("token");
        //2.如果请求头没有，就从请求体里面获取
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        return token;
    }

    /**
     * 将token字符串封装从token对象
     *
     * @param servletRequest
     * @param servletResponse
     * @return token对象
     * @throws Exception
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = getRequestToken(request);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        //将token字符串封装从token对象
        return new OAuth2Token(token);
    }

    /**
     * 判断是否被shiro处理
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return true :不会被shiro 处理 ，false：被shiro处理
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        //1.准备工作,设置响应和跨域
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        threadLocalToken.clear();

        //2.对请求token做检查
        String token = getRequestToken(request);
        if (StrUtil.isBlank(token)) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.getWriter().print("无效的令牌");
            return false;
        }

        //3.对请求token做认证
        try {
            jwtUtil.verifierToken(token);
            //3.1 检查token是否过期
        } catch (TokenExpiredException e) {
            //3.1.1 检查redis中的token是否过期,如果过期就重新生成token  (客户端过期，服务的还没过期，在5~10天之间)
            if (redisTemplate.hasKey(token)) {
                redisTemplate.delete(token);
                //通过旧的token，获取到userId信息，在重新生成token
                Integer userId = jwtUtil.getUserId(token);
                token = jwtUtil.createToken(userId);
                //将新的token重新存入redis和threadLocal中
                redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
                threadLocalToken.setToken(token);
            }
            //3.1.2 redis中的token过期了,  (客户端过期，服务也过期，在10天之后)
            else {
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.getWriter().print("令牌已经过期,请重新登录");
                return false;
            }

            //3.2 检查token的内容是否匹配
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.getWriter().print("令牌匹配错误,无效令牌");
            return false;
        }

        //3.3 通过层层检测后，执行认证与授权Realm类
        boolean executeLogin = executeLogin(request, response);
        return executeLogin;
    }

    /**
     * 登录认证失败返回详细信息
     *
     * @param token
     * @param e
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
        try {
            resp.getWriter().print(e.getMessage());
        } catch (Exception exception) {
            log.info(exception.getMessage());
        }
        return false;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        super.doFilterInternal(request, response, chain);
    }
}