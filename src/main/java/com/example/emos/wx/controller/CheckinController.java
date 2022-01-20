package com.example.emos.wx.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.CheckinForm;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.CheckinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @Program: emos-wx-api
 * @Description: 签到模块web层
 * @Author: 张鑫宇
 * @Create: 2022-01-13 20:14
 **/

@RequestMapping("/checkin")
@RestController
@Api("签到模块web层接口")
@Slf4j
public class CheckinController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CheckinService checkinService;

    @Value("${emos.image-folder}")
    private String imageFolder;

    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户当日是否签到")
    public R validCanCheckIn(@RequestHeader("token") String token) {
        Integer userId = jwtUtil.getUserId(token);

        String result = checkinService.validCanCheckIn(userId, DateUtil.today());
        return R.success(result);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinForm form, @RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {
        if (file == null) {
            return R.error("没有上传文件");
        }
        Integer userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            return R.error("请提交JPG格式图片");
        }

        //封装map
        String photoPath = imageFolder + "/" + fileName;
        try {
            //将照片存储到自定义临时硬盘中
            file.transferTo(Paths.get(photoPath));
            HashMap param = new HashMap();
            param.put("userId", userId);
            param.put("photoPath", photoPath);
            param.put("city", form.getCity());
            param.put("district", form.getDistrict());
            param.put("address", form.getAddress());
            param.put("country", form.getCountry());
            param.put("province", form.getProvince());
            checkinService.checkin(param);
            return R.success("签到成功");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new EmosException("图片保存失败");
        } finally {
            FileUtil.del(photoPath);
        }
    }

    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam MultipartFile file,@RequestHeader("token") String token){
        if (file == null) {
            return R.error("没有上传文件");
        }
        Integer userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")) {
            return R.error("请提交JPG格式图片");
        }

        //封装map
        String photoPath = imageFolder + "/" + fileName;
        try {
            //将照片存储到自定义临时硬盘中
            file.transferTo(Paths.get(photoPath));
            checkinService.createFaceModel(userId, photoPath);
            return R.success("人脸建模成功");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new EmosException("图片保存失败");
        } finally {
            FileUtil.del(photoPath);
        }

    }

}