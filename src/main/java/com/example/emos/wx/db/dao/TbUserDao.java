package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Administrator
 */
@Mapper
public interface TbUserDao {

    /**
     * 判断是否有管理员
     *
     * @return ture:有管理员，false：没有管理员
     */
    public Boolean hasRootUser();

    /**
     * 根据openId查询用户ID
     * @param openId
     * @return 大于0，就是成功
     */
    public Integer findIdByOpenId(String openId);

    /**
     * 根据userId，查询用户的权限
     * @param userId
     * @return
     */
    public Set<String> findIdByUserPermissions(Integer userId);


    /**
     * 根据userId，查询用户信息
     * @param userId
     * @return TbUser
     */
     public TbUser findAllByUserId(Integer userId);

    /**
     * 注册成为超级管理员
     * @param param 插入时所需的数据
     * @return 大于0，就是成功
     */
    public Integer insertRootUser(HashMap param);
}