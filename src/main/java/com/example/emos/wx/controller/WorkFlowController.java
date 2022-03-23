package com.example.emos.wx.controller;

import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.SearchUserTaskListByPageForm;
import com.example.emos.wx.service.MeetingService;
import com.example.emos.wx.service.WorkFlowService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Program: emos-wx-api
 * @Description:
 * @Author: 张鑫宇
 * @Create: 2022-03-17 14:36
 **/
@RestController
@RequestMapping("/workflow")
public class WorkFlowController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private WorkFlowService workFlowService;

    @PostMapping("/searchUserTaskListByPage")
    @ApiOperation("查询用户审批分页数据")
    public R searchUserTaskListByPage(@Valid @RequestBody SearchUserTaskListByPageForm form, @RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);

        Integer pageNo = form.getPageNo();
        Integer length = form.getLength();
        long start = (pageNo - 1) * length;
        String type = form.getType();
        HashMap map = new HashMap();
        map.put("userId", userId);
        map.put("start", start);
        map.put("length", length);
        map.put("type", type);
        ArrayList<HashMap> list = workFlowService.findUserTaskListByPage(map);

        return R.success().put("result", list);


    }

}