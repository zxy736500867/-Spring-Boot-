package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @Program: emos-wx-api
 * @Description: 月考勤模型数据
 * @Author: 张鑫宇
 * @Create: 2022-02-20 15:48
 **/
@Data
@ApiModel
public class SearchMonthCheckinForm {

    @NotNull
    @Range(min = 2000,max = 3000)
    private Integer year;

    @NotNull
    @Range(min=1,max=12)
    private Integer month;

}