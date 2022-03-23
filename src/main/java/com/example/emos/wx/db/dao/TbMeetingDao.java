package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
     * 查询是否为系统部门
     * @param uuid
     * @return
     */
    public Boolean findMeetingMembersInSameDept(String uuid);


    /**
     * 查询会议信息
     * @param id
     * @return
     */
    public HashMap findMeetingById(Integer id);

    /**
     * 查询会议成员
     * @param id
     * @return
     */
    public ArrayList<HashMap> findMeetingMembers(Integer id);


    /**
     * 查询用户开会的日期
     * @param param
     * @return
     */
    public List<String> findUserMeetingInMonth(HashMap param);


    /**
     * 添加会议bean信息
     *
     * @param entity
     * @return
     */
    public Integer insertMeeting(TbMeeting entity);

    /**
     * 更新会议信息
     * @param params
     * @return
     */
    public Integer updateMeetingInfo(HashMap params);

    /**
     * 删除会议记录
     * @param id
     * @return
     */
    public Integer deleteMeetingById(Integer id);

}