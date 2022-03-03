package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbDeptDao {

    /**
     * 查询部门员工集合
     * @param keyword
     * @return
     */
    public ArrayList<HashMap> findDeptMembers(String keyword);

}