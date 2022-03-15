package com.example.emos.wx.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 张鑫宇
 * 封装检测当天是否可以签到(业务层)
 * @Create 2022-01-13  14:01
 */
public interface CheckinService {

    /**
     * 检查签到
     *
     * @param userId
     * @param date
     * @return
     */
    public String validCanCheckIn(Integer userId, String date);

    /**
     * 创建人脸模型
     *
     * @param userId   用户id
     * @param filePath 上传图片的路径
     */
    public void createFaceModel(Integer userId, String filePath);

    /**
     * 签到
     *
     * @param param
     */
    public void checkin(HashMap param);

    /**
     * 查询当天签到情况
     *
     * @param userId
     * @return
     */
    public HashMap findTodayCheckinByUserId(Integer userId);

    /**
     * 查询总考勤天数
     *
     * @param userId
     * @return
     */
    public Long findCheckinByUserId(Integer userId);

    /**
     * 查询本周考勤情况
     *
     * @param param
     * @return
     */
    public ArrayList<HashMap> findWeekCheckinByParam(HashMap param);

    /**
     * 查询月考勤信息
     *
     * @param param
     * @return
     */
    public ArrayList<HashMap> findMonthCheckinByParam(HashMap param);

}
