package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Program: emos-wx-api
 * @Description: 测试表单提交验证
 * @Author: 张鑫宇
 * @Create: 2022-01-05 14:02
 **/
@ApiModel
@Data
public class TestSayHelloForm {

    //    @NotBlank
//    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,15}$")
    @ApiModelProperty("姓名")
    private String name;

}