package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Program: emos-wx-api
 * @Description: 查询消息表单
 * @Author: 张鑫宇
 * @Create: 2022-02-24 11:28
 **/
@Data
@ApiModel
public class SearchMessageByIdForm {

    @NotNull
    private String id;

}