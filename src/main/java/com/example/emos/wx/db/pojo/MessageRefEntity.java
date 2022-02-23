package com.example.emos.wx.db.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Program: emos-wx-api
 * @Description: message映射集合
 * @Author: 张鑫宇
 * @Create: 2022-02-20 21:25
 **/
@Data
@Document(collection = "message_ref")
public class MessageRefEntity implements Serializable {

    @Id
    private String _id;

    @Indexed
    private String messageId;

    @Indexed
    private Integer receiverId;

    @Indexed
    private Boolean readFlag;

    @Indexed
    private Boolean lastFlag;

}