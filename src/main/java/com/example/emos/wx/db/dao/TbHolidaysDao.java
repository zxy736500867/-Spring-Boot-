package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbHolidays;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 */
@Mapper
public interface TbHolidaysDao {

    /**
     * 是否是特殊的节假日
     * @return 1：是 ，null：不是
     */
    public Integer findTodayIsHolidays();
}