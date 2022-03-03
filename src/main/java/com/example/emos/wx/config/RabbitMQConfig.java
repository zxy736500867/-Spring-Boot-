package com.example.emos.wx.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Program: emos-wx-api
 * @Description: 消息中间件配置类
 * @Author: 张鑫宇
 * @Create: 2022-02-26 13:54
 **/
@Configuration
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory getFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        //设置部署好的Linux主机和端口号
        factory.setHost("192.168.124.101");
        factory.setPort(5672);
        return factory;
    }

}