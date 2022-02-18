package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbCheckinDao {

    /**
     * 是否签到
     *
     * @param param
     * @return
     */
    public Integer hasCheckin(HashMap param);


    /**
     * 查询当天签到情况
     * @param userId
     * @return
     */
    public HashMap findTodayCheckinByUserId(Integer userId);

    /**
     * 查询总考勤天数
     * @param userId
     * @return
     */
    public Long findCheckinByUserId(Integer userId);

    /**
     * 查询本周考勤情况
     * @param param
     * @return
     */
    public ArrayList<HashMap> findWeekCheckinByParam(HashMap param);

    /**
     * 添加签到信息
     * @param tbCheckin
     */
    public void insertAll(TbCheckin tbCheckin);
}