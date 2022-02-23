package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.MessageRefEntity;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @Program: emos-wx-api
 * @Description: 消息映射类
 * @Author: 张鑫宇
 * @Create: 2022-02-23 21:00
 **/
@Repository
public class MessageRefDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 插入messageRef实体数据
     *
     * @param messageRefEntity
     * @return
     */
    public String insertMessageRefEntity(MessageRefEntity messageRefEntity) {
        messageRefEntity = mongoTemplate.save(messageRefEntity);
        return messageRefEntity.get_id();
    }

    /**
     * 查询未读的消息数量
     *
     * @param userId
     * @return
     */
    public Long findUnreadCountByUserId(Integer userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("readFlag").is(false).and("receiverId").is(userId));
        long count = mongoTemplate.count(query, MessageRefEntity.class);
        return count;
    }

    /**
     * 查询是否是最新的消息，并且在轮询过后把最新消息变为老数据（老信息）
     *
     * @param userId
     * @return
     */
    public Long findLastCountByUserId(Integer userId) {
        //先查询
        Query query = new Query();
        query.addCriteria(Criteria.where("lastFlag").is(true).and("receiverId").is(userId));
        //后修改
        Update update = new Update();
        update.set("lastFlag", false);
        UpdateResult result = mongoTemplate.updateMulti(query, update, MessageRefEntity.class);
        long rows = result.getMatchedCount();
        return rows;
    }

    /**
     * 改变消息状态
     *
     * @param id
     * @return
     */
    public Long updateUnreadMessageById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("readFlag", true);
        UpdateResult result = mongoTemplate.updateFirst(query, update, "message_ref");
        long rows = result.getMatchedCount();
        return rows;
    }

    /**
     * 根据主键id删除MessageRef
     *
     * @param id
     * @return
     */
    public Long deleteMessageRefById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long rows = result.getDeletedCount();
        return rows;
    }

    /**
     * 根据UserId删除MessageRef
     *
     * @param userId
     * @return
     */
    public Long deleteMessageRefByUserId(Integer userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(userId));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long rows = result.getDeletedCount();
        return rows;
    }


}