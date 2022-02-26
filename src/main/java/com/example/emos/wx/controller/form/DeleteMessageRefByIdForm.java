package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Program: emos-wx-api
 * @Description: 删除messageRef消息表单
 * @Author: 张鑫宇
 * @Create: 2022-02-26 13:29
 **/
@Data
@ApiModel
public class DeleteMessageRefByIdForm {

    @NotNull
    private String id;
}