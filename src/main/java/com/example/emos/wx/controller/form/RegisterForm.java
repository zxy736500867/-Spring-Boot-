package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Program: emos-wx-api
 * @Description: 注册表单
 * @Author: 张鑫宇
 * @Create: 2022-01-09 18:42
 **/
@Data
@ApiModel
public class RegisterForm {


    @NotBlank(message = "注册码不能为空")
    @Pattern(regexp = "^[0-9]{6}$", message = "注册码必须为6位数字")
    private String invitationCode;

    @NotBlank(message = "临时授权字符串不能为空")
    private String code;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "头像不能为空")
    private String photo;

}