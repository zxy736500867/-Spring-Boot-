package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.SysConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysConfigDao {

    /**
     * 查询工作一天的考勤时间信息
     *
     * @return 上班勤时间的名称，上班时间
     */
    public List<SysConfig> findAllParam();

}