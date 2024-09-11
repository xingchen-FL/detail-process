package com.demo.detailprocess.service;

import com.alibaba.fastjson2.JSONObject;
import com.demo.detailprocess.common.ResponseVO;

public interface ProcessAccessService {
    ResponseVO createProcess(JSONObject createObj);
}
