package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Program: emos-wx-api
 * @Description: 登录表单
 * @Author: 张鑫宇
 * @Create: 2022-01-10 20:14
 **/
@Data
@ApiModel
public class LoginForm {

    @NotBlank(message = "临时授权字符串不能为空")
    private String code;

}