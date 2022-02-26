package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Program: emos-wx-api
 * @Description: 消息的分页表单
 * @Author: 张鑫宇
 * @Create: 2022-02-24 10:08
 **/
@Data
@ApiModel
public class SearchMessageByPageForm {

    @NotNull
    @Min(1)
    private Integer pageNo;

    @NotNull
    @Range(min = 1, max = 40)
    private Integer length;

}