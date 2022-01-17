package com.example.emos.wx.controller;

import cn.hutool.core.date.DateUtil;
import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.service.CheckinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Program: emos-wx-api
 * @Description: 签到模块web层
 * @Author: 张鑫宇
 * @Create: 2022-01-13 20:14
 **/

@RequestMapping("/checkin")
@RestController
@Api("签到模块web层接口")
@Slf4j
public class CheckinController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CheckinService checkinService;

    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户当日是否签到")
    public R validCanCheckIn(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);

        String result = checkinService.validCanCheckIn(userId, DateUtil.today());
        return R.success(result);
    }


}