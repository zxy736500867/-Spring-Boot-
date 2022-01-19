package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Program: emos-wx-api
 * @Description: 签到表单类
 * @Author: 张鑫宇
 * @Create: 2022-01-19 17:35
 **/
@Data
@ApiModel
public class CheckinForm {

    private String address;
    private String country;
    private String province;
    private String city;
    private String district;
}