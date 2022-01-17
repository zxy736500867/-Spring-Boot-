package com.example.emos.wx;

import cn.hutool.core.util.StrUtil;
import com.example.emos.wx.config.SystemConstants;
import com.example.emos.wx.db.dao.SysConfigDao;
import com.example.emos.wx.db.pojo.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Administrator
 */
@SpringBootApplication
@ServletComponentScan
@Slf4j
public class EmosWxApiApplication {

    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private SysConfigDao sysConfigDao;

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

    }
}
