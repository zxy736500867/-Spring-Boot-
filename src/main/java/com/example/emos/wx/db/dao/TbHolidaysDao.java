package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbHolidays;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 */
@Mapper
public interface TbHolidaysDao {

    /**
     * 是否是特殊的节假日
     *
     * @return 1：是 ，null：不是
     */
    public Integer findTodayIsHolidays();

    /**
     * 查询本周有没有特殊的节假日
     *
     * @param param
     * @return
     */
    public ArrayList<String> findHolidaysInRange(HashMap param);
}