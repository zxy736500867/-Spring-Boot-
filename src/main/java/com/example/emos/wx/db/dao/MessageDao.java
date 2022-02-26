package com.example.emos.wx.db.dao;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Program: emos-wx-api
 * @Description: 消息模块的持久层
 * @Author: 张鑫宇
 * @Create: 2022-02-23 13:41
 **/
@Repository
public class MessageDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 插入消息数据
     *
     * @param messageEntity
     * @return 主键id
     */
    public String insertMessageEntity(MessageEntity messageEntity) {
        //在mongoDB中的时间为格林尼治时间，转换为北京时间
        Date sendTime = messageEntity.getSendTime();
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, 8);
        messageEntity.setSendTime(sendTime);
        messageEntity = mongoTemplate.save(messageEntity);
        return messageEntity.get_id();
    }

    /**
     * 根据用户和分页信息查找message和message_ref 的数据
     * @param userId
     * @param start
     * @param length
     * @return
     */
    public List<HashMap> findMessageByPage(Integer userId, Long start, Integer length) {

        //先将message集合中的id转字符串：方便连接表（集合）
        JSONObject json = new JSONObject();
        json.set("$toString", "_id");

        //使用Java中对mongodb进行表（集合）连接
        //创建连接对象，并初始化连接表信息
        Aggregation aggregation = Aggregation.newAggregation(
                //将字符串message的主键id传入
                Aggregation.addFields().addField("id").withValue(json).build(),
                //进行联表（集合）
                Aggregation.lookup("message_ref", "id", "messageId", "ref"),
                //进行where过滤匹配:对收件人是登录用户做数据处理
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)),
                //排序(时间倒叙)
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "sendTime")),
                //做分页
                Aggregation.skip(start),
                Aggregation.limit(length)
        );
        //执行初始化,返回发送信息
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "message", HashMap.class);
        //返回list
        List<HashMap> list = results.getMappedResults();
        //遍历,对ref里面的有用数据进行筛选
        list.forEach(item -> {
            List<MessageRefEntity> refEntityList = (List<MessageRefEntity>) item.get("ref");
            MessageRefEntity messageRefEntity = refEntityList.get(0);
            //是否已读
            Boolean readFlag = messageRefEntity.getReadFlag();
            String refId = messageRefEntity.get_id();
            item.put("readFlag", readFlag);
            item.put("refId", refId);
            item.remove("ref");
            item.remove("_id");
            Date sendTime = (Date) item.get("sendTime");
            sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);

            String today = DateUtil.today();
            if (today.equals(DateUtil.date(sendTime).toDateStr())) {
                item.put("sendTime", DateUtil.format(sendTime, "HH:mm"));
            } else {
                item.put("sendTime", DateUtil.format(sendTime, "yyyy/MM/dd"));
            }
        });
        return list;
    }

    /**
     * 根据message集合的id，查询集合信息
     * @param id
     * @return
     */
    public HashMap findMessageById(String id) {
        //结果集
        HashMap map = mongoTemplate.findById(id, HashMap.class, "message");

        //时间转换
        Date sendTime = (Date) map.get("sendTime");
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);
        map.replace("sendTime", DateUtil.format(sendTime, "yyyy-MM-dd HH:mm"));
        return map;

    }


}