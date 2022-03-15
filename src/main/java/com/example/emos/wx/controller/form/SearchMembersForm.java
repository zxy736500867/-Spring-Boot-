package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Program: emos-wx-api
 * @Description: 查询选中员工表单
 * @Author: 张鑫宇
 * @Create: 2022-03-08 20:33
 **/
@Data
@ApiModel
public class SearchMembersForm {
    @NotBlank
    private String members;
}