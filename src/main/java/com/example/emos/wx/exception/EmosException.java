package com.example.emos.wx.exception;

import lombok.Data;

/**
 * @Program: emos-wx-api
 * @Description: 自定义异常类
 * @Author: 张鑫宇
 * @Create: 2022-01-04 21:57
 **/
@Data
public class EmosException extends RuntimeException {
    private String  msg;
    private Integer code = 500;

    public EmosException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public EmosException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public EmosException(String msg, Integer code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public EmosException(String msg, Integer code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}