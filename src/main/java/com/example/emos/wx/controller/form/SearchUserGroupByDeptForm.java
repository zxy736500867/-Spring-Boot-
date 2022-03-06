package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @Program: emos-wx-api
 * @Description:
 * @Author: 张鑫宇
 * @Create: 2022-03-03 10:40
 **/
@Data
@ApiModel
public class SearchUserGroupByDeptForm {

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,15}$")
    private String keyWord;

}