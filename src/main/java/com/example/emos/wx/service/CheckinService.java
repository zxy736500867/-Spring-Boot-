package com.example.emos.wx.service;

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
}
