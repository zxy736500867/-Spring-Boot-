package com.example.emos.wx.controller;

import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.DeleteMessageRefByIdForm;
import com.example.emos.wx.controller.form.SearchMessageByIdForm;
import com.example.emos.wx.controller.form.SearchMessageByPageForm;
import com.example.emos.wx.controller.form.UpdateUnreadMessageForm;
import com.example.emos.wx.service.MessageService;
import com.example.emos.wx.task.MessageTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * @Program: emos-wx-api
 * @Description: 消息控制层
 * @Author: 张鑫宇
 * @Create: 2022-02-24 10:54
 **/
@RestController
@RequestMapping("/message")
@Api("消息模块网络接口")
public class MessageController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageTask messageTask;

    @PostMapping("/searchMessageByPage")
    @ApiOperation("获取分页消息列表")
    public R searchMessageByPage(@Valid @RequestBody SearchMessageByPageForm form, @RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        Integer pageNo = form.getPageNo();
        Integer length = form.getLength();
        Long start = Long.valueOf((pageNo - 1) * length);
        List<HashMap> list = messageService.findMessageByPage(userId, start, length);
        return R.success().put("result", list);
    }

    @PostMapping("/searchMessageById")
    @ApiOperation("根据主键id查询消息")
    public R searchMessageById(@Valid @RequestBody SearchMessageByIdForm form) {
        HashMap map = messageService.findMessageById(form.getId());
        return R.success().put("result", map);
    }

    @PostMapping("/updateUnreadMessage")
    @ApiOperation("将未读消息更新成已读消息")
    public R updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessageForm form) {
        Long rows = messageService.updateUnreadMessageById(form.getId());
        return R.success().put("result", rows == 1 ? true : false);
    }

    @PostMapping("deleteMessageRefById")
    @ApiOperation("删除消息")
    public R deleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdForm form) {
        Long rows = messageService.deleteMessageRefById(form.getId());
        return R.success().put("result", rows == 1 ? true : false);
    }

    @GetMapping("/refreshMessage")
    @ApiOperation("轮询刷新用户消息")
    public R refreshMessage(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        //发送异步消息
        messageTask.receiveAsync(userId + "");
        //获取最新接收消息条数
        Long lastRows = messageService.findLastCountByUserId(userId);
        //获取未读消息条数
        Long unreadRows = messageService.findUnreadCountByUserId(userId);
        return R.success().put("lastRows", lastRows).put("unreadRows", unreadRows);
    }

}