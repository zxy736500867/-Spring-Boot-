package com.example.emos.wx.service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author 张鑫宇
 * @Create 2022-03-17  15:36
 */
public interface WorkFlowService {

    public ArrayList<HashMap> findUserTaskListByPage(HashMap params);
}
