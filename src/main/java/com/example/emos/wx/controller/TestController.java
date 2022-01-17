package com.example.emos.wx.controller;

import com.example.emos.wx.common.util.R;
import com.example.emos.wx.controller.form.TestSayHelloForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Program: emos-wx-api
 * @Description: 测试controller接口
 * @Author: 张鑫宇
 * @Create: 2022-01-05 11:56
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/sayHello")
    @ApiOperation("最简单的测试方法了")
    public R sayHello(@Valid @RequestBody TestSayHelloForm form){
        return R.success().put("msg", "Swagger第一个测试接口成功！！！"+form.getName());
    }
}