package com.example.emos.wx.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import com.example.emos.wx.db.dao.TbMeetingDao;
import com.example.emos.wx.db.pojo.TbMeeting;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Program: emos-wx-api
 * @Description: 会议业务层
 * @Author: 张鑫宇
 * @Create: 2022-03-02 12:49
 **/
@Service
@Slf4j
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private TbMeetingDao meetingDao;

    @Override
    public ArrayList<HashMap> findMyMeetingListByPage(HashMap paras) {
        ArrayList<HashMap> list = meetingDao.findMyMeetingListByPage(paras);
        //对数据库的返回结果list进行处理
        ArrayList resultList = new ArrayList();
        HashMap resultMap = null;
        JSONArray array = null;
        //前一条会议日期
        String date = null;
        //分组，对日期、会议列表分组
        for (HashMap item : list) {
            //获取会议日期
            String temp = item.get("date").toString();
            //如果前一条会议日期与遍历循环的日期数据不相等，就创建新的会议列表记录
            if (!temp.equals(date)) {
                date = temp;
                //开启新的小列表
                resultMap = new HashMap();
                resultMap.put("date", date);
                array = new JSONArray();
                resultMap.put("list", array);
                resultList.add(resultMap);
            }
            array.put(item);
        }
        return resultList;
    }

    @Override
    public HashMap findMeetingById(Integer id) {
        HashMap map = meetingDao.findMeetingById(id);
        ArrayList<HashMap> list = meetingDao.findMeetingMembers(id);
        map.put("members", list);
        return map;
    }

    @Override
    public List<String> findUserMeetingInMonth(HashMap param) {
        List<String> list = meetingDao.findUserMeetingInMonth(param);
        return list;
    }

    @Override
    public void insertMeeting(TbMeeting tbMeeting) {
        Integer row = meetingDao.insertMeeting(tbMeeting);
        if (row != 1) {
            throw new EmosException("会议添加失败");
        }
        //TODO 开启审批工作流
    }

    @Override
    public void updateMeetingInfo(HashMap params) {
        Integer row = meetingDao.updateMeetingInfo(params);
        if (row != 1) {
            throw new EmosException("会议更新失败");
        }
    }

    @Override
    public void deleteMeetingById(Integer id) {
        HashMap meeting = meetingDao.findMeetingById(id);
        //会议开始时间
        DateTime startTime = DateUtil.parse(meeting.get("date") + " " + meeting.get("start"));
        //当前系统时间
        DateTime now = DateUtil.date();

        if (now.isAfterOrEquals(startTime.offset(DateField.MINUTE, -10))) {
            throw new EmosException("距离会议开始不足10分钟，不能取消本次会议");
        }

        Integer row = meetingDao.deleteMeetingById(id);
        if (row != 1) {
            throw new EmosException("会议删除失败");
        }

    }
}