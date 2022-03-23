package com.example.emos.wx.service;

import com.example.emos.wx.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author 张鑫宇
 * @Create 2022-03-02  12:47
 */
public interface MeetingService {

    /**
     * 查询会议分页信息
     *
     * @param paras
     * @return
     */
    public ArrayList<HashMap> findMyMeetingListByPage(HashMap paras);

    /**
     * 查询会议信息
     * @param id
     * @return
     */
    public HashMap findMeetingById(Integer id);

    /**
     * 查询用户开会的日期
     * @param param
     * @return
     */
    public List<String> findUserMeetingInMonth(HashMap param);

    /**
     * 添加会议bean信息
     *
     * @param tbMeeting
     */
    public void insertMeeting(TbMeeting tbMeeting);


    /**
     * 更新会议信息
     * @param params
     * @return
     */
    public void updateMeetingInfo(HashMap params);

    /**
     * 删除会议记录
     * @param id
     * @return
     */
    public void deleteMeetingById(Integer id);
}
