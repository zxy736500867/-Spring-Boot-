package com.example.emos.wx.controller;

import cn.hutool.core.io.FileUtil;
import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.TestSayHelloForm;
import com.example.emos.wx.exception.EmosException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @Program: emos-wx-api
 * @Description: 测试controller接口
 * @Author: 张鑫宇
 * @Create: 2022-01-05 11:56
 **/
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @PostMapping("/sayHello")
    @ApiOperation("最简单的测试方法了")
    public R sayHello(@Valid @RequestBody TestSayHelloForm form) {
        return R.success().put("msg", "Swagger第一个测试接口成功！！！" + form.getName());
    }

}