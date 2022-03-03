package com.example.emos.wx;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;
import com.example.emos.wx.db.pojo.TbMeeting;
import com.example.emos.wx.service.MeetingService;
import com.example.emos.wx.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class EmosWxApiApplicationTests {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MeetingService meetingService;

    @Test
    void contextLoads() {
        for (int i = 1; i <= 100; i++) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setUuid(IdUtil.simpleUUID());
            messageEntity.setSenderId(0);
            messageEntity.setSenderName("系统消息");
            messageEntity.setMsg("这是第" + i + "条测试消息");
            messageEntity.setSendTime(new Date());
            String id = messageService.insertMessageEntity(messageEntity);

            MessageRefEntity messageRefEntity = new MessageRefEntity();
            messageRefEntity.setMessageId(id);
            messageRefEntity.setReceiverId(22);
            messageRefEntity.setLastFlag(true);
            messageRefEntity.setReadFlag(false);
            messageService.insertMessageRefEntity(messageRefEntity);
        }
    }

    @Test
    void createMeetingData() {
        for (int i = 1; i <= 100; i++) {
            TbMeeting meeting = new TbMeeting();
            meeting.setId((long) i);
            meeting.setUuid(IdUtil.simpleUUID());
            meeting.setTitle("测试会议" + i);
            meeting.setCreatorId(22L);  //超级管理员主键id
            meeting.setDate(DateUtil.today());
            meeting.setPlace("线上会议室");
            meeting.setStart("08:30");
            meeting.setEnd("10:30");
            meeting.setType((short) 1);
            meeting.setMembers("[22,23]");
            meeting.setDesc("会议研讨Emos项目上线测试");
            meeting.setInstanceId(IdUtil.simpleUUID());
            meeting.setStatus((short) 3);
            meetingService.insertMeeting(meeting);

        }
    }

}
