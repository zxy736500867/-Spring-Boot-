package com.example.emos.wx.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Program: emos-wx-api
 * @Description: 异步邮箱发送
 * @Author: 张鑫宇
 * @Create: 2022-01-20 11:37
 **/
@Component
@Scope("prototype")
public class EmailTask implements Serializable {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${emos.email.system}")
    private String systemSender;

    /**
     * 异步邮箱发送信息
     *
     * @param message
     */
    @Async
    public void sendAsync(SimpleMailMessage message) {
        message.setFrom(systemSender);
        //防止被163捕获为垃圾邮件，需要将邮件抄送给自己，验证不为垃圾邮件
        //message.setCc(systemSender);
        javaMailSender.send(message);
    }
}