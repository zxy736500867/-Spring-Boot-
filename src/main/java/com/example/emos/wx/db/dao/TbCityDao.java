package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbCity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbCityDao {

    /**
     * 通过城市名称查询城市编码
     *
     * @param cityName
     * @return code
     */
    public String findCode(String cityName);
}