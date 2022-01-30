package com.example.emos.wx.service.impl;

import com.example.emos.wx.db.dao.*;
import com.example.emos.wx.db.pojo.TbFaceModel;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.BaiduApiService;
import com.example.emos.wx.utils.BaseUtil;
import com.example.emos.wx.utils.GsonUtils;
import com.example.emos.wx.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @Program: emos-wx-api
 * @Description: 调用百度人脸识别接口
 * @Author: 张鑫宇
 * @Create: 2022-01-27 11:28
 **/
@Service
@Scope("prototype")
@Slf4j
public class BaiduApiServiceImpl implements BaiduApiService {

    @Autowired
    private TbFaceModelDao faceModelDao;

    @Value("${baidu.api-key}")
    private String apiKey;

    @Value("${baidu.secret-key}")
    private String securetKey;

    @Value("${baidu.authHost}")
    private String authHost;

    @Value("${baidu.addUrl}")
    private String addUrl;

    @Value("${baidu.searchUrl}")
    private String searchUrl;


    /**
     * 获取API访问token
     *
     * @param apiKey     百度云官网获取的 API Key
     * @param securetKey 百度云官网获取的 Securet Key
     * @return
     */
    @Override
    public final String getBaiduToken(String apiKey, String securetKey) {
        String url = authHost + "grant_type=client_credentials"
                + "&client_id=" + apiKey
                + "&client_secret=" + securetKey;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            log.info("access_token===" + access_token);
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

    /**
     * 向自己创建的百度库中添加人脸注册
     *
     * @param userId   用户id
     * @param filePath 上传图片的路径
     * @return
     */
    @Override
    public void addUserFace(Integer userId, String filePath) {
        log.info("向百度api发送创建人脸模型请求，携带图片信息");
        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
        String accessToken = getBaiduToken(apiKey, securetKey);
        // 请求url
        String url = addUrl;

        // 照片转义成base64编码
        String image = BaseUtil.encryptToBase64(filePath);

        // 封装请求体的数据--》map
        Map<String, Object> map = new HashMap<>();
        map.put("image", image);
        map.put("group_id", "1");
        map.put("user_id", userId.toString());
        map.put("user_info", "测试用户");
        map.put("liveness_control", "NORMAL");
        map.put("image_type", "BASE64");
        map.put("quality_control", "NORMAL");
        String param = GsonUtils.toJson(map);

        String response = null;
        try {
            response = HttpUtil.post(url, accessToken, "application/json", param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) {
            return;
        }
        String error_msg = (String) new JSONObject(response).get("error_msg");
        if ("pic not has face".equals(error_msg)) {
            throw new EmosException("pic未识别出人脸，请重试", 500);
        } else if ("liveness check fail".equals(error_msg)) {
            throw new EmosException("活体检测识别失败，请勿使用图片上传人脸信息", 500);
        } else if ("face is fuzzy".equals(error_msg)) {
            throw new EmosException("人脸模型模糊，请重试", 500);
        } else {
            Map mapJson = new JSONObject(response).getJSONObject("result").toMap();
            String face_token = (String) mapJson.get("face_token");

            TbFaceModel faceModelEntity = new TbFaceModel();
            faceModelEntity.setUserId(userId);
            faceModelEntity.setFaceModel(face_token);
            //对bean进行插入
            faceModelDao.insertAll(faceModelEntity);
        }
    }

    /**
     * 向自己创建的百度库中搜索对比存在的人脸
     *
     * @param userId
     * @param filePath
     * @return
     */
    @Override
    public Map<String, Object> searchFace(Integer userId, String filePath) {
        log.info("向百度api发送对比人脸模型请求，携带图片信息");
        String accessToken= getBaiduToken(apiKey, securetKey);

        // 请求url
        String url = searchUrl;
        String image = BaseUtil.encryptToBase64(filePath);

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", image);
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", "0,1,2,3,4,5,6,7,8,9");
            map.put("user_id", userId.toString());
            map.put("image_type", "BASE64");
            map.put("quality_control", "NORMAL");
            map.put("max_user_num", 1);

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。

            String response = HttpUtil.post(url, accessToken, "application/json", param);
            log.info(response);

            //一对{}，一个JSONObject对象
            JSONObject jsonResponse = new JSONObject(response);
            Integer error_code = (Integer) jsonResponse.get("error_code");
            String error_msg = (String) jsonResponse.get("error_msg");

            String face_token = jsonResponse.getJSONObject("result").getString("face_token");
            List user_list = jsonResponse.getJSONObject("result").getJSONArray("user_list").toList();

            String group_id = (String) ((HashMap) user_list.get(0)).get("group_id");
            String user_id = (String) ((HashMap) user_list.get(0)).get("user_id");
            String user_info = (String) ((HashMap) user_list.get(0)).get("user_info");
            Double score = (Double) ((HashMap) user_list.get(0)).get("score");


            HashMap<String, Object> responseMap = new HashMap<>(7);
            responseMap.put("error_code", error_code);
            responseMap.put("error_msg", error_msg);
            responseMap.put("face_token", face_token);
            responseMap.put("group_id", group_id);
            responseMap.put("user_id", user_id);
            responseMap.put("user_info", user_info);
            responseMap.put("score", score);

            return responseMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}