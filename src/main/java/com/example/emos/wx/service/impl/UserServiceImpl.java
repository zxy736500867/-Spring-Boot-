package com.example.emos.wx.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.wx.config.shiro.OAuth2Filter;
import com.example.emos.wx.db.dao.TbDeptDao;
import com.example.emos.wx.db.dao.TbUserDao;
import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.TbUser;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.UserService;
import com.example.emos.wx.task.MessageTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * @Program: emos-wx-api
 * @Description: 实现注册超级管理员功能（业务层)
 * @Author: 张鑫宇
 * @Create: 2022-01-09 12:29
 **/
@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {

    //管理员的注册码
    public static final String INVITATION_CODE = "000000";

    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private MessageTask messageTask;

    @Autowired
    private TbDeptDao deptDao;

    /**
     * 获取临时登录凭证
     * @param code
     * @return
     */
    private String getOpenId(String code) {
        log.info("code====="+code);

        String url = "https://api.weixin.qq.com/sns/jscode2session";

        HashMap<String, Object> map = new HashMap<>();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        log.info("json====="+json);
        String openId = json.getStr("openid");
        if (openId == null || openId.length() == 0) {
            throw new RuntimeException("临时登录凭证错误");
        }
        return openId;
    }

    /**
     * 注册用户
     * @param invitationCode  注册时的邀请码
     * @param code            临时授权字符串
     * @param nickname        微信昵称
     * @param photo           微信头像的url地址
     * @return userId         用户ID
     */
    @Override
    public Integer insertUser(String invitationCode, String code, String nickname, String photo) {

        if (INVITATION_CODE.equals(invitationCode)) {
            Boolean hasRootBool = userDao.hasRootUser();
            if (!hasRootBool) {
                String openId = getOpenId(code);
                HashMap<String, Object> param = new HashMap<>();
                param.put("openId", openId);
                param.put("nickname", nickname);
                param.put("photo", photo);
                param.put("role", "[0]");
                param.put("status", 1);
                param.put("createTime", new Date());
                param.put("root", true);
                userDao.insertRootUser(param);
                Integer userId = userDao.findIdByOpenId(openId);
                // 注册成功后，发送系统消息
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setSenderId(0);
                messageEntity.setSenderName("系统信息");
                messageEntity.setUuid(IdUtil.simpleUUID());
                messageEntity.setMsg("欢迎您注册成为超级管理员，请及时更新您的员工个人信息。");
                messageEntity.setSendTime(new Date());
                messageTask.sendAsync(userId + "", messageEntity);
                return userId;
            }
            //已经存在了管理员了
            else {
                throw new EmosException("已存在管理员用户，无法再次绑定用户");
            }
        }
        //TODO 普通用户注册
        else {
            String openId = getOpenId(code);
            HashMap<String, Object> param = new HashMap<>();
            param.put("openId", openId);
            param.put("nickname", nickname);
            param.put("photo", photo);
            param.put("role", "[0]");
            param.put("status", 1);
            param.put("createTime", new Date());
            param.put("root", true);
            userDao.insertRootUser(param);
            Integer userId = userDao.findIdByOpenId(openId);
            // 注册成功后，发送系统消息
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setSenderId(0);
            messageEntity.setSenderName("系统信息");
            messageEntity.setUuid(IdUtil.simpleUUID());
            messageEntity.setMsg("欢迎您注册成为emos员工，请及时更新您的员工个人信息。");
            messageEntity.setSendTime(new Date());
            messageTask.sendAsync(userId + "", messageEntity);
            return userId;

        }

    }

    /**
     * 根据userId，查询用户的权限
     * @param userId
     * @return 权限信息
     */
    @Override
    public Set<String> findIdByUserPermissions(Integer userId) {
        Set<String> permissions = userDao.findIdByUserPermissions(userId);
        return permissions;
    }

    /**
     * 根据userId，查询用户信息
     * @param userId
     * @return TbUser
     */
    @Override
    public TbUser findAllByUserId(Integer userId) {
        TbUser user = userDao.findAllByUserId(userId);
        return user;
    }

    /**
     * 查询用户的入职日期
     * @param userId
     * @return
     */
    @Override
    public String findHiredateByUserId(Integer userId) {
        String hireDate = userDao.findHiredateByUserId(userId);
        return hireDate;
    }

    /**
     * 查询用户姓名头像和部门
     * @param userId
     * @return
     */
    @Override
    public HashMap findUserSummaryByUserId(Integer userId) {
        HashMap map = userDao.findUserSummaryByUserId(userId);
        return map;
    }

    /**
     * 将员工归档到部门列表下
     * @param keyword
     * @return
     */
    @Override
    public ArrayList<HashMap> findUserGroupByDept(String keyword) {

        //部门列表
        ArrayList<HashMap> deptMembers = deptDao.findDeptMembers(keyword);
        //员工列表
        ArrayList<HashMap> userGroupByDept = userDao.findUserGroupByDept(keyword);

        //遍历部门列表
        for (HashMap deptItem : deptMembers) {
            long deptId = (Long) deptItem.get("id");
            ArrayList members = new ArrayList();

            //遍历员工列表
            for (HashMap userItem : userGroupByDept) {
                long udId = (Long) userItem.get("deptId");
                if (deptId==udId) {
                    members.add(userItem);
                }
            }
            //向部门添加员工
            deptItem.put("members", members);
        }

        return deptMembers;
    }

    @Override
    public Integer login(String code) {

        String openId = getOpenId(code);
        Integer userId = userDao.findIdByOpenId(openId);
        if (userId==null){
            throw new EmosException("员工用户不存在");
        }
        // 从消息队列中接收消息，转移到消息表
//        messageTask.receiveAsync(userId + "");
        return userId;
    }
}