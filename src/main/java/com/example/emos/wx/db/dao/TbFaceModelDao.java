package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbFaceModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbFaceModelDao {

    /**
     * 通过userId找到人脸模型字符串，签到时用
     *
     * @param userId
     * @return 人脸模型字符串
     */
    public String findFaceModelByUserId(Integer userId);

    /**
     * 先上传到人脸信息到数据库，才能打卡
     *
     * @param tbFaceModel
     */
    public void insertAll(TbFaceModel tbFaceModel);

    /**
     * 删除人脸信息，管理员删除离职人员
     *
     * @param userId
     * @return
     */
    public Integer deleteAll(Integer userId);


}