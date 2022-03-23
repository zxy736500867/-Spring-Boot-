package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Program: emos-wx-api
 * @Description:
 * @Author: 张鑫宇
 * @Create: 2022-03-17 09:53
 **/
@Data
@ApiModel
public class SearchMeetingByIdForm {

    @NotNull
    @Min(1)
    private Integer id;


}