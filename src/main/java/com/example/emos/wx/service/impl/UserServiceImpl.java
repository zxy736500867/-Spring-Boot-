package com.example.emos.wx.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.wx.config.shiro.OAuth2Filter;
import com.example.emos.wx.db.dao.TbUserDao;
import com.example.emos.wx.db.pojo.TbUser;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
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
                return userId;
            }
            //已经存在了管理员了
            else {
                throw new EmosException("已存在管理员用户，无法再次绑定用户");
            }
        }
        //TODO 普通用户注册
        else {

        }

        return null;
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

    @Override
    public Integer login(String code) {

        String openId = getOpenId(code);
        Integer userId = userDao.findIdByOpenId(openId);
        if (userId==null){
            throw new EmosException("员工用户不存在");
        }
        //TODO 从消息队列中接收消息，转移到消息表
        return userId;
    }
}