package com.example.emos.wx.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 张鑫宇
 * @Create 2022-01-27  11:24
 */
public interface BaiduApiService {

    /**
     * 获取API访问token
     * @param apiKey 百度云官网获取的 API Key
     * @param securetKey 百度云官网获取的 Securet Key
     * @return  assess_token
     */
    public String getBaiduToken(String apiKey,String securetKey);

    /**
     * 向自己创建的百度库中添加人脸注册
     * @param userId            用户id
     * @param filePath      上传图片的路径
     * @return
     */
    public void addUserFace(Integer userId, String filePath);

    /**
     * 向自己创建的百度库中搜索对比存在的人脸
     */
    public Map searchFace(Integer userId, String photoPathStr);


;}
