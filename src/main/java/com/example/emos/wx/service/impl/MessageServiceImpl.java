package com.example.emos.wx.service.impl;

import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;
import com.example.emos.wx.service.MessageService;
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

    @Override
    public String insertMessageEntity(MessageEntity messageEntity) {
        return null;
    }

    @Override
    public List<HashMap> findMessageByPage(Integer userId, Long start, Integer length) {
        return null;
    }

    @Override
    public HashMap findMessageById(String id) {
        return null;
    }

    @Override
    public String insertMessageRefEntity(MessageRefEntity messageRefEntity) {
        return null;
    }

    @Override
    public Long findUnreadCountByUserId(Integer userId) {
        return null;
    }

    @Override
    public Long findLastCountByUserId(Integer userId) {
        return null;
    }

    @Override
    public Long updateUnreadMessageById(String id) {
        return null;
    }

    @Override
    public Long deleteMessageRefById(String id) {
        return null;
    }

    @Override
    public Long deleteMessageRefByUserId(Integer userId) {
        return null;
    }
}