package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

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
}