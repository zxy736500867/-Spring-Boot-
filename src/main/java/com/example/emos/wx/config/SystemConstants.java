package com.example.emos.wx.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @Program: emos-wx-api
 * @Description: 考勤信息类
 * @Author: 张鑫宇
 * @Create: 2022-01-13 11:22
 **/
@Data
@Component
public class SystemConstants {

    /**
     * 上班考勤开始时间
     */
    public String attendanceStartTime;

    /**
     * 上班时间
     */
    public String attendanceTime;

    /**
     * 上班考勤截止时间
     */
    public String attendanceEndTime;

    /**
     * 下班考勤开始时间
     */
    public String closingStartTime;

    /**
     * 下班考勤截止时间
     */
    public String closingEndTime;

}