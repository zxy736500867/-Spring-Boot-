package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 */
@Mapper
public interface TbMeetingDao {

    /**
     * 查询会议分页信息
     *
     * @param paras
     * @return
     */
    public ArrayList<HashMap> findMyMeetingListByPage(HashMap paras);


    /**
     * 添加会议bean信息
     *
     * @param entity
     * @return
     */
    public Integer insertMeeting(TbMeeting entity);


}