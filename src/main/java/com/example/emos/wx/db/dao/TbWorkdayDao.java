package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbWorkday;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 */
@Mapper
public interface TbWorkdayDao {

    /**
     * 是否是特殊工作日
     * @return 1：是 ，null：不是
     */
    public Integer findTodayIsWorkday();

    /**
     * 查询本周有没有特殊的工作日
     * @param param
     * @return
     */
    public ArrayList<String> findWorkdaysInRange(HashMap param);
}