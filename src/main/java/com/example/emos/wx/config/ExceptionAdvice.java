package com.example.emos.wx.config;

import com.example.emos.wx.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @Program: emos-wx-api
 * @Description: 精简返回给客户端的异常
 * @Author: 张鑫宇
 * @Create: 2022-01-06 16:12
 **/
@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        log.error("执行异常", e);
        //1.后端数据验证异常
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            return exception.getBindingResult().getFieldError().getDefaultMessage();
        }
        //2.EmosException自定义的异常类
        else if (e instanceof EmosException) {
            EmosException exception = (EmosException) e;
            return exception.getMsg();
        }
        //3. 授权异常
        else if (e instanceof UnauthenticatedException) {
            return "您不具备相关权限";
        }
        //4.普通异常
        else {
            return "后端执行异常！请及时联系管理员";
        }

    }
}