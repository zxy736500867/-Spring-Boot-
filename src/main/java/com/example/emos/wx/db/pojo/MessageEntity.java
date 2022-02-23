package com.example.emos.wx.db.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @Program: emos-wx-api
 * @Description: message集合
 * @Author: 张鑫宇
 * @Create: 2022-02-20 20:44
 **/

@Data
@Document(collection = "message")
public class MessageEntity implements Serializable {

    @Id
    private String _id;

    @Indexed(unique = true)
    private String uuid;

    @Indexed
    private Integer senderId;

    private String senderPhoto = "https://public-1304729818.cos.ap-nanjing.myqcloud.com/img/header/10.jpg";

    private String senderName;

    @Indexed
    private Date sendTime;

    private String msg;

}