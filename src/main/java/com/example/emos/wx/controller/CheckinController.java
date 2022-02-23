package com.example.emos.wx.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.SystemConstants;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.CheckinForm;
import com.example.emos.wx.controller.form.SearchMonthCheckinForm;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.BaiduApiService;
import com.example.emos.wx.service.CheckinService;
import com.example.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

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

    @Autowired
    private BaiduApiService baiduApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConstants constants;

    @Value("${emos.image-folder}")
    private String imageFolder;

    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户当日是否签到")
    public R validCanCheckIn(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckIn(userId, DateUtil.today());
        return R.success(result);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinForm form, @RequestParam("photo") MultipartFile filePath, @RequestHeader("token") String token) {
        if (filePath == null) {
            return R.error("没有上传文件");
        }
        Integer userId = jwtUtil.getUserId(token);
        String fileName = filePath.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            return R.error("请提交JPG格式图片");
        }

        //封装map
        String photoPath = imageFolder + "/" + fileName;
        try {
            //将照片存储到自定义临时硬盘中
            filePath.transferTo(Paths.get(photoPath));
            HashMap param = new HashMap();
            param.put("userId", userId);
            param.put("photoPath", photoPath);
            param.put("city", form.getCity());
            param.put("district", form.getDistrict());
            param.put("address", form.getAddress());
            param.put("country", form.getCountry());
            param.put("province", form.getProvince());
            checkinService.checkin(param);
            return R.success("签到成功");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new EmosException("图片保存失败");
        } finally {
            FileUtil.del(photoPath);
        }
    }

    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile filePath, @RequestHeader("token") String token) {
        log.info("创建人脸模型");
        if (filePath == null) {
            return R.error("没有上传文件");
        }
        Integer userId = jwtUtil.getUserId(token);
        String fileName = filePath.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            return R.error("请提交JPG格式图片");
        }

        //封装map
        String photoPath = imageFolder + "/" + fileName;
        try {
            //将照片存储到自定义临时硬盘中
            filePath.transferTo(Paths.get(photoPath));
            baiduApiService.addUserFace(userId, photoPath);
            return R.success("人脸建模成功");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new EmosException("图片保存失败");
        } finally {
            FileUtil.del(photoPath);
        }

    }

    @GetMapping("/searchTodayCheckin")
    @ApiOperation("查询用户当日签到数据")
    public R searchTodayCheckin(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);
        //查询当天签到情况
        HashMap map = checkinService.findTodayCheckinByUserId(userId);
        //上班时间
        map.put("attendanceTime", constants.attendanceTime);
        //下班考勤截止时间
        map.put("closingEndTime", constants.closingEndTime);
        //查询总考勤天数
        Long days = checkinService.findCheckinByUserId(userId);
        map.put("checkinDays", days);

        //查询用户的入职日期转化为对象（string=》日期对象）
        DateTime hiredate = DateUtil.parse(userService.findHiredateByUserId(userId));
        //本周开始日期对象
        DateTime startDate = DateUtil.beginOfWeek(DateUtil.date());
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }
        //本周结束日期对象
        DateTime endDate = DateUtil.endOfWeek(DateUtil.date());
        HashMap param = new HashMap();
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());
        param.put("userId", userId);
        ArrayList<HashMap> list = checkinService.findWeekCheckinByParam(param);
        map.put("weekCheckin", list);
        return R.success().put("result", map);
    }

    @PostMapping("searchMonthCheckin")
    @ApiOperation("查询用户某月签到数据")
    public R searchMonthCheckin(@RequestHeader("token") String token, @Valid @RequestBody SearchMonthCheckinForm form) {
        Integer userId = jwtUtil.getUserId(token);
        //获取入职那一天的日期对象
        DateTime hiredate = DateUtil.parse(userService.findHiredateByUserId(userId));
        //将月份转化为两位数字（1月-》01月）
        String month = form.getMonth() < 10 ? "0" + form.getMonth() : form.getMonth().toString();
        //获取查询月份的1号
        DateTime startDate = DateUtil.parse(form.getYear() + "-" + month + "-01");

        //  DateUtil.beginOfMonth(hiredate) 入职时本月的第一天对象（1号）
        if (startDate.isBefore(DateUtil.beginOfMonth(hiredate))) {
            throw new EmosException("只能查询入职之后考勤数据");
        }

        //将入职考勤的那一天作为本月的开始时间
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }

        //本月考勤最后一天
        DateTime endDate = DateUtil.endOfMonth(startDate);
        HashMap param = new HashMap();
        param.put("userId", userId);
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());

        ArrayList<HashMap> list = checkinService.findMonthCheckinByParam(param);

        Integer normalState = 0;
        Integer lateState = 0;
        Integer absenceState = 0;
        for (HashMap<String, String> item : list) {
            //考勤类型
            String type = item.get("type");
            //考勤状态
            String status = item.get("status");
            if ("工作日".equals(type)) {
                if ("正常".equals(status)) {
                    normalState++;
                } else if ("迟到".equals(status)) {
                    lateState++;
                } else if ("缺勤".equals(status)) {
                    absenceState++;
                }
            }
        }
        return R.success().put("list", list).put("normalState", normalState).put("lateState", lateState).put("absenceState", absenceState);
    }
}