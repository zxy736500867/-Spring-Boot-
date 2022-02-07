package com.example.emos.wx.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.emos.wx.config.SystemConstants;
import com.example.emos.wx.db.dao.*;
import com.example.emos.wx.db.pojo.TbCheckin;
import com.example.emos.wx.db.pojo.TbFaceModel;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.BaiduApiService;
import com.example.emos.wx.service.CheckinService;
import com.example.emos.wx.task.EmailTask;
import com.example.emos.wx.utils.BaseUtil;
import com.example.emos.wx.utils.GsonUtils;
import com.example.emos.wx.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Program: emos-wx-api
 * @Description: 检测当天是否可以签到
 * @Author: 张鑫宇
 * @Create: 2022-01-13 14:03
 **/
@Service
@Scope("prototype")
@Slf4j
public class CheckinServiceImpl implements CheckinService {


    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private TbHolidaysDao holidaysDao;

    @Autowired
    private TbWorkdayDao workdayDao;

    @Autowired
    private TbCheckinDao checkinDao;

    @Autowired
    private TbFaceModelDao faceModelDao;

    @Autowired
    private TbCityDao cityDao;

    @Autowired
    private EmailTask emailTask;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private BaiduApiService baiduApiService;

    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;

    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;

    @Value("${emos.email.hr}")
    private String hrEmail;


    /**
     * 验证当天能否签到
     *
     * @param userId
     * @param date
     * @return
     */
    @Override
    public String validCanCheckIn(Integer userId, String date) {

        Boolean isHoliday = (holidaysDao.findTodayIsHolidays() != null ? true : false);
        Boolean isWorkday = (workdayDao.findTodayIsWorkday() != null ? true : false);

        String type = "工作日";
        if (DateUtil.date().isWeekend()) {
            type = "节假日";
        }

        if (isHoliday) {
            type = "节假日";
        }
        if (isWorkday) {
            type = "工作日";
        }
        if ("节假日".equals(type)) {
            return "节假日无需考勤";
        } else {
            DateTime now = DateUtil.date();
            String checkStart = DateUtil.today() + " " + systemConstants.attendanceStartTime;
            String checkEnd = DateUtil.today() + " " + systemConstants.attendanceEndTime;
            //开始考勤时间
            DateTime attendanceStart = DateUtil.parse(checkStart);
            //结束考勤时间
            DateTime attendanceEnd = DateUtil.parse(checkEnd);

            if (now.isBefore(attendanceStart)) {
                return "现在还早呢！考勤时间是早上6：00哦";
            } else if (now.isAfter(attendanceEnd)) {
                return "现在是" + now + ",您已经超过了" + DateUtil.today() + " 9：30的考勤时间";
            } else {
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", checkStart);
                map.put("end", checkEnd);

                Boolean isCheck = (checkinDao.hasCheckin(map) != null ? true : false);
                return isCheck ? "今日已经完成考勤，请勿重复考勤！" : "考勤记录中";
            }
        }
    }

    /**
     * 创建人脸模型
     *
     * @param userId   用户id
     * @param filePath 上传图片的路径
     */
    @Override
    public void createFaceModel(Integer userId, String filePath) {
        baiduApiService.addUserFace(userId, filePath);
    }

    /**
     * 执行签到
     *
     * @param param
     */
    @Override
    public void checkin(HashMap param) {

        //定义当天上班的状态：正常，迟到，旷工
        Byte status = 1;

        //当前打卡时间
        Date nowTime = DateUtil.date();
        //上班时间 8：30
        Date attendanceTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceTime);
        //上班考勤截止时间 9：30
        Date attendanceEndTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime);

        //当天上班的状态：正常 status=1
        if (nowTime.compareTo(attendanceTime) <= 0) {
            status = 1;
        }
        //当天上班的状态：迟到 status=2
        else if (nowTime.compareTo(attendanceTime) > 0 && nowTime.compareTo(attendanceEndTime) < 0) {
            status = 2;
        }
        Integer userId = (Integer) param.get("userId");
        log.info("userid===" + userId);
        //获取人脸模型字符串
        String faceModelStr = faceModelDao.findFaceModelStrByUserId(userId);
        log.info("faceModelStr===" + faceModelStr);
        if (faceModelStr == null) {
            throw new EmosException("不存在人脸模型");
        } else {
            //TODO 有点问题：异常处理和responseMap封装，最后再看
            //获取到考勤拍照时要上传的照片路径
            String photoPath = (String) param.get("photoPath");
            Map responseMap = baiduApiService.searchFace(userId, photoPath);

            if (responseMap == null || (Integer) responseMap.get("error_code") != 0) {
                throw new EmosException("人脸识别服务异常！");
            }
            //对响应体的数据进行分解
            String error_msg = (String) responseMap.get("error_msg");
            if ("match user is not found".equals(error_msg) || "照片中存在多张人脸".equals(error_msg)) {
                throw new EmosException("人脸识别不匹配！");
            } else if ("face is fuzzy".equals(error_msg)) {
                throw new EmosException("人脸是模糊！");
            } else if ("liveness check fail".equals(error_msg)) {
                throw new EmosException("活性检查失败！");
            } else if ("SUCCESS".equals(error_msg)) {
                //TODO 疫情风险等级
                // 设置疫情风险等级 risk=1：低风险 2:中风险  3：高风险
                Integer risk = 1;
                String city = (String) param.get("city");
                String district = (String) param.get("district");
                String address = (String) param.get("address");
                String country = (String) param.get("country");
                String province = (String) param.get("province");

                if ((!StrUtil.isBlank(city)) && (!StrUtil.isBlank(district))) {
                    String code = cityDao.findCode(city);
                    try {
                        String riskUrl = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                        //通过jsoup解析
                        Document documentHTML = Jsoup.connect(riskUrl).get();
                        Elements elements = documentHTML.getElementsByClass("list-content");
                        if (elements.size() > 0) {
                            Element element = elements.get(0);
                            String riskText = element.select("p:last-child").text();
//                            riskText="高风险";
                            if ("高风险".equals(riskText)) {
                                risk = 3;
                                //TODO 发送告警邮件
                                HashMap<String, String> map = userDao.findNameAndDeptByUserId(userId);
                                String name = map.get("name");
                                String deptName = map.get("dept_name");
                                deptName = (deptName != null ? deptName : "");
                                //设置邮件内容信息
                                SimpleMailMessage message = new SimpleMailMessage();
                                //设置收件人hrEmail
                                message.setTo(hrEmail);
                                message.setSubject("员工" + name + "身处高风险疫情地区警告");
                                message.setText(deptName + "员工" + name + "," + DateUtil.format(new Date(), "yyyy年MM月dd日") + "处于" +
                                        address + ",属于新冠疫情高风险地区,请及时联系该员工,核实情况！");
                                emailTask.sendAsync(message);
                            } else if ("中风险".equals(riskText)) {
                                risk = 2;
                            } else {
                                risk = 1;
                            }
                        }
                    } catch (Exception e) {
                        log.error("执行异常", e);
                        throw new EmosException("获取地区风险等级失败");
                    }

                }

                //TODO 保存签到记录,时间转换还有点问题
                TbCheckin checkinEntity = new TbCheckin();
                checkinEntity.setUserId(userId);
                checkinEntity.setAddress(address);
                checkinEntity.setCountry(country);
                checkinEntity.setProvince(province);
                checkinEntity.setCity(city);
                checkinEntity.setDistrict(district);
                checkinEntity.setStatus(status);
                checkinEntity.setDate(DateUtil.parse(DateUtil.today()));
                checkinEntity.setCreateTime(nowTime);
                checkinDao.insertAll(checkinEntity);
            }

        }


    }


}