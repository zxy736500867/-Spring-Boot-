package com.example.emos.wx.task;

import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.MessageService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Program: emos-wx-api
 * @Description: 消息任务
 * @Author: 张鑫宇
 * @Create: 2022-02-26 21:14
 **/
@Component
@Slf4j
public class MessageTask {

    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private MessageService messageService;

    /**
     * 同步发送消息
     *
     * @param topic
     * @param messageEntity
     */
    public void send(String topic, MessageEntity messageEntity) {

        //获取插入消息的id
        String id = messageService.insertMessageEntity(messageEntity);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //对topic进行系统设置(队列名称,是否保存到硬盘持久化,加锁,线程执行完是否自动删除,参数)
            channel.queueDeclare(topic, true, false, false, null);

            HashMap map = new HashMap();
            map.put("messageId", id);
            //将消息messageId放入请求头
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(map).build();
            channel.basicPublish("", topic, properties, messageEntity.getMsg().getBytes());
            log.debug("消息发送成功");

        } catch (Exception e) {
            log.error("消息任务执行异常", e);
            throw new EmosException("向MQ发送消息失败");

        }
    }

    /**
     * 异步发送消息
     *
     * @param topic
     * @param messageEntity
     */
    @Async
    public void sendAsync(String topic, MessageEntity messageEntity) {
        send(topic, messageEntity);
    }

    /**
     * 同步接收消息
     *
     * @param topic
     * @return
     */
    public Integer receive(String topic) {
        Integer rows = 0;

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //对topic进行系统设置(队列名称,是否保存到硬盘持久化,加锁,线程执行完是否自动删除,参数)
            channel.queueDeclare(topic, true, false, false, null);

            //循环遍历拿出消息，直到找到对应的消息
            while (true) {
                //设置请求响应，且设置手动ack应答：false
                GetResponse response = channel.basicGet(topic, false);
                if (response != null) {
                    //获取请求头消息内容
                    AMQP.BasicProperties properties = response.getProps();
                    Map<String, Object> map = properties.getHeaders();
                    String messageId = map.get("messageId").toString();

                    //获取请求头内容
                    byte[] body = response.getBody();
                    String message = new String(body);
                    log.debug("从RabbitMQ中接收的消息：" + message);

                    //将消息封装
                    MessageRefEntity messageRefEntity = new MessageRefEntity();
                    messageRefEntity.setMessageId(messageId);
                    messageRefEntity.setReceiverId(Integer.parseInt(topic));
                    messageRefEntity.setReadFlag(false);
                    messageRefEntity.setLastFlag(true);
                    messageService.insertMessageRefEntity(messageRefEntity);

                    //设置手动ack应答
                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    channel.basicAck(deliveryTag, false);
                    rows++;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("消息任务执行异常", e);
            throw new EmosException("接收消息失败");
        }
        return rows;
    }

    /**
     * 异步接收消息
     *
     * @param topic
     * @return
     */
    @Async
    public Integer receiveAsync(String topic) {
        return receive(topic);
    }


    /**
     * 同步删除消息队列
     *
     * @param topic
     */
    public void deleteQueue(String topic) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //对topic进行系统设置(队列名称,是否保存到硬盘持久化,加锁,线程执行完是否自动删除,参数)
            channel.queueDeclare(topic, true, false, false, null);

            channel.queueDelete(topic);
            log.debug("消息队列成功删除");

        } catch (Exception e) {
            log.error("删除队列失败", e);
            throw new EmosException("删除队列失败");

        }
    }

    /**
     * 异步删除消息队列
     *
     * @param topic
     */
    @Async
    public void deleteQueueAsync(String topic) {
        deleteQueue(topic);
    }


}