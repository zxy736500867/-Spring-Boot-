package com.example.emos.wx.service.impl;

import com.example.emos.wx.db.dao.MessageDao;
import com.example.emos.wx.db.dao.MessageRefDao;
import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;
import com.example.emos.wx.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @Program: emos-wx-api
 * @Description: 消息业务层
 * @Author: 张鑫宇
 * @Create: 2022-02-23 22:21
 **/
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MessageRefDao messageRefDao;

    @Override
    public String insertMessageEntity(MessageEntity messageEntity) {
        String id = messageDao.insertMessageEntity(messageEntity);
        return id;
    }

    @Override
    public List<HashMap> findMessageByPage(Integer userId, Long start, Integer length) {
        List<HashMap> list = messageDao.findMessageByPage(userId, start, length);
        return list;
    }

    @Override
    public HashMap findMessageById(String id) {
        HashMap map = messageDao.findMessageById(id);
        return map;
    }

    @Override
    public String insertMessageRefEntity(MessageRefEntity messageRefEntity) {
        String id = messageRefDao.insertMessageRefEntity(messageRefEntity);
        return id;
    }

    @Override
    public Long findUnreadCountByUserId(Integer userId) {
        Long count = messageRefDao.findUnreadCountByUserId(userId);
        return count;
    }

    @Override
    public Long findLastCountByUserId(Integer userId) {
        Long count = messageRefDao.findLastCountByUserId(userId);
        return count;
    }

    @Override
    public Long updateUnreadMessageById(String id) {
        Long rows = messageRefDao.updateUnreadMessageById(id);
        return rows;
    }

    @Override
    public Long deleteMessageRefById(String id) {
        Long rows = messageRefDao.deleteMessageRefById(id);
        return rows;
    }

    @Override
    public Long deleteMessageRefByUserId(Integer userId) {
        Long rows = messageRefDao.deleteMessageRefByUserId(userId);
        return rows;
    }
}