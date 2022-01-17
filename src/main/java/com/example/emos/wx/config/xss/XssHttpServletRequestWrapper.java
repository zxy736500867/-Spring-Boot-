package com.example.emos.wx.config.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Program: emos-wx-api
 * @Description: 定义请求包装类:对传入的数据去标签，返回文本
 * @Author: 张鑫宇
 * @Create: 2022-01-05 15:56
 **/

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 对字符串做解析
     *
     * @param name
     * @return
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (!StrUtil.hasEmpty(value)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    /**
     * 对数组做解析
     *
     * @param name
     * @return
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (!StrUtil.hasEmpty(value)) {
                    value = HtmlUtil.filter(value);
                }
                values[i] = value;
            }
        }
        return values;
    }

    /**
     * 对map集合做解析
     *
     * @return
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters = super.getParameterMap();
        LinkedHashMap<String, String[]> map = new LinkedHashMap<>();
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                String[] values = parameters.get(key);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        String value = values[i];
                        if (!StrUtil.hasEmpty(value)) {
                            value = HtmlUtil.filter(value);
                        }
                        values[i] = value;
                    }
                }
                map.put(key, values);
            }
        }
        return map;
    }

    /**
     * 对请求头数据进行解析
     *
     * @param name
     * @return
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (!StrUtil.hasEmpty(value)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        //1.从流中读取数据
        InputStream in = super.getInputStream();
        InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
        BufferedReader buffer = new BufferedReader(reader);
        StringBuffer body = new StringBuffer();
        String line = buffer.readLine();
        while (line != null) {
            body.append(line);
            line = buffer.readLine();
        }

        buffer.close();
        reader.close();
        in.close();

        //2.将数据转为map遍历解析
        Map<String, Object> map = JSONUtil.parseObj(body.toString());
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            Object val = map.get(key);
            if (val instanceof String) {
                if (!StrUtil.hasEmpty(val.toString())) {
                    result.put(key, HtmlUtil.filter(val.toString()));
                }
            } else {
                result.put(key, val);
            }
        }

        //3.将封装好的map数据，以流的形式返回
        String json = JSONUtil.toJsonStr(result);
        ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes());
        //4.通过匿名对象返回，重要实现read()的重写
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };

    }


}