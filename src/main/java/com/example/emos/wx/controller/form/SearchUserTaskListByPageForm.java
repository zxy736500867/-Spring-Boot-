package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Program: emos-wx-api
 * @Description:
 * @Author: 张鑫宇
 * @Create: 2022-03-17 14:40
 **/
@Data
@ApiModel
public class SearchUserTaskListByPageForm {
    @NotNull
    @Min(1)
    private Integer pageNo;

    @NotNull
    @Range(min = 1, max = 40)
    private Integer length;

    @NotBlank
    private String type;

}