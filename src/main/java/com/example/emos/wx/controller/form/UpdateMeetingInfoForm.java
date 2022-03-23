package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Program: emos-wx-api
 * @Description:
 * @Author: 张鑫宇
 * @Create: 2022-03-17 11:31
 **/
@Data
@ApiModel
public class UpdateMeetingInfoForm {

    @NotBlank
    private String title;

    @NotNull
    private String date;

    private String place;

    @NotNull
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
    private String start;

    @NotNull
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
    private String end;

    @Range(min = 1, max = 2)
    private Byte type;

    @NotBlank
    private String members;

    @NotBlank
    private String desc;

    @Min(1)
    private Integer id;

}