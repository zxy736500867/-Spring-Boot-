package com.example.emos.wx;

import cn.hutool.core.util.StrUtil;
import com.example.emos.wx.config.SystemConstants;
import com.example.emos.wx.db.dao.SysConfigDao;
import com.example.emos.wx.db.pojo.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Administrator
 */
@SpringBootApplication
@ServletComponentScan
@EnableAsync
@Slf4j
public class EmosWxApiApplication {

    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Value("${emos.image-folder}")
    private String imageFolder;

    public static void main(String[] args) {
        SpringApplication.run(EmosWxApiApplication.class, args);
    }

    @PostConstruct
    public void init() {

        List<SysConfig> sysConfigList = sysConfigDao.findAllParam();
        sysConfigList.forEach(item -> {
            String key = item.getParamKey();
            key = StrUtil.toCamelCase(key);
            String value = item.getParamValue();

            try {
                Field field = systemConstants.getClass().getDeclaredField(key);
                field.set(systemConstants, value);
            } catch (Exception e) {
                log.error("考勤信息异常", e);
            }
        });
        new File(imageFolder).mkdirs();
    }
}
