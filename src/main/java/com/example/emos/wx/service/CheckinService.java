package com.example.emos.wx.service;

import java.util.HashMap;

/**
 * @Author 张鑫宇
 *  封装检测当天是否可以签到(业务层)
 * @Create 2022-01-13  14:01
 */
public interface CheckinService {

    /**
     * 检查签到
     * @param userId
     * @param date
     * @return
     */
    public String validCanCheckIn(Integer userId,String date);

    /**
     * 签到信息
     * @param param
     */
    public void checkin(HashMap param);

    /**
     * 创建人脸模型
     * @param userId
     * @param photoPathStr
     */
    public void createFaceModel(Integer userId,String photoPathStr);
}
