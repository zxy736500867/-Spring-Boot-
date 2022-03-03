package com.example.emos.wx.service;

import com.example.emos.wx.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author 张鑫宇
 * @Create 2022-03-02  12:47
 */
public interface MeetingService {

    /**
     * 查询会议分页信息
     * @param paras
     * @return
     */
    public ArrayList<HashMap> findMyMeetingListByPage(HashMap paras);

    /**
     * 添加会议bean信息
     * @param tbMeeting
     */
    public void insertMeeting(TbMeeting tbMeeting);
}
