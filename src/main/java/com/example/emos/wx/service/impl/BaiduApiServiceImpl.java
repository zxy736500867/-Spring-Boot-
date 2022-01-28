package com.example.emos.wx.service.impl;


import cn.hutool.core.codec.Base64Decoder;
import com.example.emos.wx.db.dao.TbFaceModelDao;
import com.example.emos.wx.db.pojo.TbFaceModel;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.BaiduApiService;
import com.example.emos.wx.utils.BaseUtil;
import com.example.emos.wx.utils.GsonUtils;
import com.example.emos.wx.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${baidu.api-key}")
    private String apiKey;

    @Value("${baidu.secret-key}")
    private String securetKey;

    @Autowired
    private TbFaceModelDao faceModelDao;

    /**
     * 获取token地址
     */
    String authHost = "https://aip.baidubce.com/oauth/2.0/token?";

    /**
     * 获取API访问token
     * @param apiKey 百度云官网获取的 API Key
     * @param securetKey 百度云官网获取的 Securet Key
     * @return
     */
    @Override
    public final String getBaiduToken(String apiKey, String securetKey) {
        String url=authHost+ "grant_type=client_credentials"
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
            log.info("access_token==="+access_token);
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

    @Override
    public void addUserFace(Integer userId, String photoPathStr) {
        log.info("向百度api发送创建人脸模型请求，携带图片信息");
        String baiduToken = getBaiduToken(apiKey, securetKey);
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";

        String image = BaseUtil.encryptToBase64(photoPathStr);

        try {

            Map<String, Object> map = new HashMap<>();
            map.put("image",image);
            map.put("group_id", "1");
            map.put("user_id", userId.toString());
            map.put("user_info", "测试用户");
            map.put("liveness_control", "NORMAL");
            map.put("image_type", "BASE64");
            map.put("quality_control", "NORMAL");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = baiduToken;

            String response = HttpUtil.post(url, accessToken, "application/json", param);
            log.info(response);

            Map mapJson= new JSONObject(response).getJSONObject("result").toMap();
            String face_token = (String) mapJson.get("face_token");
            if (face_token==null) {
                throw new EmosException("人脸注册失败");
            } else {
                TbFaceModel faceModelEntity = new TbFaceModel();
                faceModelEntity.setUserId(userId);
                faceModelEntity.setFaceModel(face_token);
                //对bean进行插入
                faceModelDao.insertAll(faceModelEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}