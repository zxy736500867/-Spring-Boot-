package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @Program: emos-wx-api
 * @Description:
 * @Author: 张鑫宇
 * @Create: 2022-03-22 14:34
 **/
@Data
@ApiModel
public class SearchUserMeetingInMonthForm {

    @Range(min = 2000, max = 9999)
    private Integer year;

    @Range(min = 1, max = 12)
    private Integer month;
}