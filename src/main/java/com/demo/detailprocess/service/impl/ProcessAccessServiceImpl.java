package com.demo.detailprocess.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.demo.detailprocess.service.ProcessAccessService;
import org.flowable.engine.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProcessAccessServiceImpl implements ProcessAccessService {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private IdentityService identityService;
    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;
    @Override
    public String createProcess(JSONObject createObj) {
        return "";
    }
}
