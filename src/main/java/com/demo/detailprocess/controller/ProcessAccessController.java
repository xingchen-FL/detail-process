package com.demo.detailprocess.controller;

import com.alibaba.fastjson2.JSONObject;
import com.demo.detailprocess.common.ResponseVO;
import com.demo.detailprocess.service.ProcessAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("process")
@RestController
public class ProcessAccessController {
    @Autowired
    private ProcessAccessService processAccessService;


    @PostMapping("/createProcess")
    public ResponseVO createProcess(JSONObject createObj) {
        String process = processAccessService.createProcess(createObj);
        return ResponseVO.success(process);
    }
}
