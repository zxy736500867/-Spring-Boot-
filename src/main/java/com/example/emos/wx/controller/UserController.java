package com.example.emos.wx.controller;

import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.LoginForm;
import com.example.emos.wx.controller.form.RegisterForm;
import com.example.emos.wx.controller.form.SearchUserGroupByDeptForm;
import com.example.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Program: emos-wx-api
 * @Description: 用户模块
 * @Author: 张鑫宇
 * @Create: 2022-01-09 19:37
 **/
@RestController
@RequestMapping("/user")
@Api("用户模块web接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.expire}")
    private Integer cacheExpire;

    /**
     * 在redis中保存token信息
     *
     * @param token
     * @param userId
     */
    private void saveCacheToken(String token, Integer userId) {
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }

    /**
     * 注册用户
     *
     * @param form
     * @return R对象
     */
    @PostMapping("/register")
    @ApiOperation("注册用户")
    public R register(@Valid @RequestBody RegisterForm form) {
        Integer userId = userService.insertUser(form.getInvitationCode(), form.getCode(), form.getNickname(), form.getPhoto());
        String token = jwtUtil.createToken(userId);
        Set<String> permissionSet = userService.findIdByUserPermissions(userId);
        saveCacheToken(token, userId);
        return R.success("用户注册成功").put("token", token).put("permissionSet", permissionSet);
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public R login(@Valid @RequestBody LoginForm form) {
        Integer userId = userService.login(form.getCode());
        String token = jwtUtil.createToken(userId);
        saveCacheToken(token, userId);
        Set<String> permissionSet = userService.findIdByUserPermissions(userId);
        return R.success("登录成功").put("token", token).put("permissionSet", permissionSet);
    }

    @GetMapping("/searchUserSummary")
    @ApiOperation("查询用户摘要信息")
    public R searchUserSummary(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        HashMap map = userService.findUserSummaryByUserId(userId);
        return R.success().put("result", map);
    }

    @PostMapping("/searchUserGroupByDept")
    @ApiOperation("查询员工列表，按照部门分组排列")
    @RequiresPermissions(value = {"ROOT", "EMPLOYEE:SELECT"}, logical = Logical.OR)
    public R searchUserGroupByDept(@Valid @RequestBody SearchUserGroupByDeptForm form) {
        ArrayList<HashMap> list = userService.findUserGroupByDept(form.getKeyWord());
        return R.success().put("result", list);
    }
}