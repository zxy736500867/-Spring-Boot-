package com.example.emos.wx.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.emos.wx.config.SystemConstants;
import com.example.emos.wx.db.dao.TbCheckinDao;
import com.example.emos.wx.db.dao.TbFaceModelDao;
import com.example.emos.wx.db.dao.TbHolidaysDao;
import com.example.emos.wx.db.dao.TbWorkdayDao;
import com.example.emos.wx.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @Program: emos-wx-api
 * @Description: 检测当天是否可以签到
 * @Author: 张鑫宇
 * @Create: 2022-01-13 14:03
 **/
@Service
@Scope("prototype")
@Slf4j
public class CheckinServiceImpl implements CheckinService {


    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private TbHolidaysDao holidaysDao;

    @Autowired
    private TbWorkdayDao workdayDao;

    @Autowired
    private TbCheckinDao checkinDao;

    @Autowired
    private TbFaceModelDao faceModelDao;

    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;

    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;

    @Override
    public String validCanCheckIn(Integer userId, String date) {

        Boolean isHoliday = (holidaysDao.findTodayIsHolidays() != null ? true : false);
        Boolean isWorkday = (workdayDao.findTodayIsWorkday() != null ? true : false);

        String type = "工作日";
        if (DateUtil.date().isWeekend()) {
            type = "节假日";
        }

        if (isHoliday) {
            type = "节假日";
        }
        if (isWorkday) {
            type = "工作日";
        }
        if ("节假日".equals(type)) {
            return "节假日无需考勤";
        } else {
            DateTime now = DateUtil.date();
            String checkStart = DateUtil.today() + " " + systemConstants.attendanceStartTime;
            String checkEnd = DateUtil.today() + " " + systemConstants.attendanceEndTime;
            //开始考勤时间
            DateTime attendanceStart = DateUtil.parse(checkStart);
            //结束考勤时间
            DateTime attendanceEnd = DateUtil.parse(checkEnd);

            if (now.isBefore(attendanceStart)) {
                return "现在还早呢！考勤时间是早上6：00哦";
            } else if (now.isAfter(attendanceEnd)) {
                return "现在是" + now + ",您已经超过了"+DateUtil.today()+" 9：30的考勤时间";
            } else {
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", checkStart);
                map.put("end", checkEnd);

                Boolean isCheck = (checkinDao.hasCheckin(map) != null ? true : false);
                return isCheck ? "今日已经完成考勤，请勿重复考勤！" : "考勤记录中";
            }
        }
    }

    @Override
    public void checkin(HashMap param) {

    }
}