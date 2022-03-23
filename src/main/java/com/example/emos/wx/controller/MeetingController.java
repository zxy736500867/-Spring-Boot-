package com.example.emos.wx.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.*;
import com.example.emos.wx.db.pojo.TbMeeting;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.MeetingService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @Program: emos-wx-api
 * @Description: 会议信息web层
 * @Author: 张鑫宇
 * @Create: 2022-03-02 17:11
 **/
@RestController
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MeetingService meetingService;


    @PostMapping("/searchMyMeetingListByPage")
    @ApiOperation("查询会议列表分页数据")
    public R searchMyMeetingListByPage(@Valid @RequestBody SearchMyMeetingListByPageForm form, @RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);

        Integer pageNo = form.getPageNo();
        Integer length = form.getLength();
        long start = (pageNo - 1) * length;

        HashMap map = new HashMap();
        map.put("userId", userId);
        map.put("start", start);
        map.put("length", length);
        ArrayList<HashMap> list = meetingService.findMyMeetingListByPage(map);
        return R.success().put("result", list);
    }


    @PostMapping("/insertMeeting")
    @ApiOperation("添加会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT"}, logical = Logical.OR)
    public R insertMeeting(@Valid @RequestBody InsertMeetingForm form, @RequestHeader("token") String token) {
        //判断会议地点是否合理
        if (form.getType() == 2 && (form.getPlace() == null || form.getPlace().length() == 0)) {
            throw new EmosException("线下会议地点不能为空");
        }

        //判断会议时间是否合理
        DateTime startTime = DateUtil.parse(form.getDate() + " " + form.getStart() + ":00");
        DateTime endTime = DateUtil.parse(form.getDate() + " " + form.getEnd() + ":00");
        if (endTime.isBeforeOrEquals(startTime)) {
            throw new EmosException("会议结束时间必须在开始时间之后");
        }

        //判断开会人员必须是数组格式
        if (!JSONUtil.isJsonArray(form.getMembers())) {
            throw new EmosException("参会人员必须是JSON数组");
        }

        //插入数据
        TbMeeting entity = new TbMeeting();
        entity.setUuid(UUID.randomUUID().toString());
        entity.setTitle(form.getTitle());
        entity.setCreatorId((long) jwtUtil.getUserId(token));
        entity.setDate(form.getDate());
        entity.setPlace(form.getPlace());
        entity.setStart(form.getStart() + ":00");
        entity.setEnd(form.getEnd() + ":00");
        entity.setType((short) form.getType());
        entity.setMembers(form.getMembers());
        entity.setDesc(form.getDesc());
        entity.setStatus((short) 3);
        meetingService.insertMeeting(entity);
        return R.success().put("result", "success");

    }

    @PostMapping("/searchMeetingById")
    @ApiOperation("根据Id查询会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT"}, logical = Logical.OR)
    public R searchMeetingById(@Valid @RequestBody SearchMeetingByIdForm form) {
        HashMap map = meetingService.findMeetingById(form.getId());
        return R.success().put("result", map);
    }

    @PostMapping("/searchUserMeetingInMonth")
    @ApiOperation("查询某月用户的会议日期列表")
    public R searchUserMeetingInMonth(@Valid @RequestBody SearchUserMeetingInMonthForm form, @RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        HashMap params = new HashMap();
        params.put("userId", userId);
        params.put("express", form.getYear() + "/" + form.getMonth());
        List<String> list = meetingService.findUserMeetingInMonth(params);
        return R.success().put("result",list);
    }


    @PostMapping("/updateMeetingInfo")
    @ApiOperation("更新会议信息")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT"}, logical = Logical.OR)
    public R updateMeetingInfo(@Valid @RequestBody UpdateMeetingInfoForm form) {
        //判断会议地点是否合理
        if (form.getType() == 2 && (form.getPlace() == null || form.getPlace().length() == 0)) {
            throw new EmosException("线下会议地点不能为空");
        }

        //判断会议时间是否合理
        DateTime startTime = DateUtil.parse(form.getDate() + " " + form.getStart() + ":00");
        DateTime endTime = DateUtil.parse(form.getDate() + " " + form.getEnd() + ":00");
        if (endTime.isBeforeOrEquals(startTime)) {
            throw new EmosException("会议结束时间必须在开始时间之后");
        }

        //判断开会人员必须是数组格式
        if (!JSONUtil.isJsonArray(form.getMembers())) {
            throw new EmosException("参会人员必须是JSON数组");
        }

        //修改数据
        HashMap param=new HashMap();
        param.put("title", form.getTitle());
        param.put("date", form.getDate());
        param.put("place", form.getPlace());
        param.put("start", form.getStart() + ":00");
        param.put("end", form.getEnd() + ":00");
        param.put("type", form.getType());
        param.put("members", form.getMembers());
        param.put("desc", form.getDesc());
        param.put("id", form.getId());
        param.put("status", 3);
        meetingService.updateMeetingInfo(param);
        return R.success().put("result","success");
    }

    @PostMapping("/deleteMeetingById")
    @ApiOperation("根据ID删除会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:DELETE"}, logical = Logical.OR)
    public R deleteMeetingById(@Valid @RequestBody DeleteMeetingByIdForm form){
        meetingService.deleteMeetingById(form.getId());
        return R.success().put("result","success");
    }


}