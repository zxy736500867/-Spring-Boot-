package com.example.emos.wx.controller;

import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.SearchMyMeetingListByPageForm;
import com.example.emos.wx.service.MeetingService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

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

}