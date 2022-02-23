package com.example.emos.wx.service;

import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @Author 张鑫宇
 * @Create 2022-02-23  22:17
 */
public interface MessageService {
    /**
     * 插入消息数据
     *
     * @param messageEntity
     * @return
     */
    public String insertMessageEntity(MessageEntity messageEntity);

    /**
     * 根据用户和分页信息查找message和message_ref 的数据
     *
     * @param userId
     * @param start
     * @param length
     * @return
     */
    public List<HashMap> findMessageByPage(Integer userId, Long start, Integer length);

    /**
     * 根据message集合的id，查询集合信息
     *
     * @param id
     * @return
     */
    public HashMap findMessageById(String id);

    /**
     * 插入messageRef实体数据
     *
     * @param messageRefEntity
     * @return
     */
    public String insertMessageRefEntity(MessageRefEntity messageRefEntity);

    /**
     * 查询未读的消息数量
     *
     * @param userId
     * @return
     */
    public Long findUnreadCountByUserId(Integer userId);

    /**
     * 查询是否是最新的消息，并且在轮询过后把最新消息变为老数据（老信息）
     *
     * @param userId
     * @return
     */
    public Long findLastCountByUserId(Integer userId);

    /**
     * 改变消息状态
     *
     * @param id
     * @return
     */
    public Long updateUnreadMessageById(String id);

    /**
     * 根据主键id删除MessageRef
     *
     * @param id
     * @return
     */
    public Long deleteMessageRefById(String id);

    /**
     * 根据UserId删除MessageRef
     *
     * @param userId
     * @return
     */
    public Long deleteMessageRefByUserId(Integer userId);


}
