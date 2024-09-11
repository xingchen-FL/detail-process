package com.demo.detailprocess.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.demo.detailprocess.common.ResponseVO;
import com.demo.detailprocess.listener.FinalListener;
import com.demo.detailprocess.listener.PushMsgListener;
import com.demo.detailprocess.service.ProcessAccessService;
import com.demo.detailprocess.vo.BusinessVo;
import com.demo.detailprocess.vo.DataTractTaskVo;
import com.demo.detailprocess.vo.NodeVo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.demo.detailprocess.common.ProcessConstant.*;

@Service
@Slf4j
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
    public ResponseVO createProcess(JSONObject createObj) {
        DataTractTaskVo dataTractTaskVo = JSONObject.parseObject(createObj.toString(), DataTractTaskVo.class);
        String check = dataTractTaskVo.check();
        if (StringUtils.isNotBlank(check)) {
            return ResponseVO.failed(check);
        }
        BusinessVo creator = dataTractTaskVo.getCreator();
        String userId = creator.getUserId();
        String businessKey = creator.getBusinessType();
        String relationId = creator.getRelationId();
        String callBackUrl = dataTractTaskVo.getCallBackUrl();
        String dataStr = JSON.toJSONString(dataTractTaskVo);
        deploy(dataTractTaskVo);
        identityService.setAuthenticatedUserId(userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(businessKey,businessKey, JSONObject.parseObject(dataTractTaskVo.toString()));
        Task result = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).taskAssignee(userId).singleResult();

        return null;
    }


    public void deploy(DataTractTaskVo data) {
        log.warn("deploy is:{}", JSONObject.toJSONString(data));
        List<NodeVo> dataProcess = data.getProcess();
        BusinessVo creator = data.getCreator();
        String userId = creator.getUserId();
        String businessType = creator.getBusinessType();
        String relationId = creator.getRelationId();

        Process process = new Process();
        process.setId(businessType);
        String startEventId = generateId();
        String creatorId = generateId();
        String endId = generateId();

        StartEvent startEvent = new StartEvent();
        startEvent.setId(startEventId);
        startEvent.setName(START_EVENT_NAME);
        startEvent.setFormKey(relationId);

        UserTask startUserTask = new UserTask();
        startUserTask.setId(creatorId);
        startUserTask.setName(FIRST_NODE_NAME);
        startUserTask.setAssignee(userId);
        startUserTask.setFormKey(relationId);

        SequenceFlow flow1 = generateFlow(process, startEventId, creatorId);
        startEvent.setOutgoingFlows(Lists.newArrayList(flow1));
        startUserTask.setIncomingFlows(Lists.newArrayList(flow1));
        process.addFlowElement(startEvent);
        process.addFlowElement(startUserTask);

        createEndEvent(endId, process);

        String firstInId = null;
        String prevOutId = null;
        UserTask prevTask = new UserTask();
        List<UserTask> tasks = new ArrayList<>();
        LinkedList<NodeVo> sortData = dataProcess.stream().sorted(Comparator.comparingInt(NodeVo::getNumber)).collect(Collectors.toCollection(LinkedList::new));

        JSONObject nodesVar = new JSONObject();
        for (int i = 0; i < sortData.size(); i++) {
            NodeVo nodeVo = sortData.get(i);
            String currentId = generateId();
            if (i == 0) {
                firstInId = currentId;
            }
            UserTask currentTask = new UserTask();
            currentTask.setId(currentId);
            currentTask.setName(nodeVo.getName());
            String assigneeStr = nodeVo.getUserId();
            String[] split = StringUtils.split(assigneeStr, ",");
            List<String> assigneeList = Arrays.stream(split).distinct().collect(Collectors.toList());
            String varList = currentId.replace("-", "")+"List";
            nodesVar.fluentPut(varList, assigneeList);
            MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
            multiInstanceLoopCharacteristics.setSequential(false);
            multiInstanceLoopCharacteristics.setInputDataItem("${nodesVar."+varList+"}");
            multiInstanceLoopCharacteristics.setElementVariable("assignee");
            multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances/nrOfInstances == 1}");
            currentTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
            currentTask.setAssignee("${assignee}");
            List<FlowableListener> taskListeners = Lists.newArrayList();
            FlowableListener pushMsgListener = new FlowableListener();
            pushMsgListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
            pushMsgListener.setEvent(COMPLETE_EVENT_NAME);
            pushMsgListener.setImplementation(PushMsgListener.class.getName());
            taskListeners.add(pushMsgListener);
            currentTask.setTaskListeners(taskListeners);
            currentTask.setFormKey(nodeVo.getApprovalType());
            if (prevOutId != null) {
                SequenceFlow incomingFlow = generateFlow(process, prevOutId, currentId);
                currentTask.setIncomingFlows(Lists.newArrayList(incomingFlow));
                if (ObjectUtils.isNotEmpty(prevTask)) {
                    prevTask.setOutgoingFlows(Lists.newArrayList(incomingFlow));
                }
            }
            tasks.add(currentTask);
            prevOutId = currentId;
            prevTask = currentTask;
        }
        data.setNodesVar(nodesVar);
        for (UserTask task : tasks) {
            if (CollectionUtils.isEmpty(task.getIncomingFlows())) {
                SequenceFlow inMultiFlow = generateFlow(process, creatorId, firstInId);
                task.setIncomingFlows(Lists.newArrayList(inMultiFlow));
            }
            if (CollectionUtils.isEmpty(task.getOutgoingFlows())) {
                SequenceFlow outMultiFlow = generateFlow(process, prevOutId, endId);
                task.setOutgoingFlows(Lists.newArrayList(outMultiFlow));
            }
            process.addFlowElement(task);
        }
        BpmnModel model = new BpmnModel();
        model.addProcess(process);
        repositoryService.createDeployment().addBpmnModel(businessType + BPMN_SUFFIX, model).deploy();
    }

    private static void createEndEvent(String endId, Process process) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(endId);
        endEvent.setName(END_EVENT_NAME);
        List<FlowableListener> executionListener = Lists.newArrayList();
        FlowableListener finalListener = new FlowableListener();
        finalListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        finalListener.setEvent(START_EXE_NAME);
        finalListener.setImplementation(FinalListener.class.getName());
        executionListener.add(finalListener);
        endEvent.setExecutionListeners(executionListener);
        process.addFlowElement(endEvent);
    }


    private static String generateId() {
        return "sid-" + UUID.randomUUID();
    }

    private static SequenceFlow generateFlow(Process process, String startId, String endId) {
        SequenceFlow currentFlow = new SequenceFlow();
        currentFlow.setId(generateId());
        currentFlow.setSourceRef(startId);
        currentFlow.setTargetRef(endId);
        process.addFlowElement(currentFlow);
        return currentFlow;
    }
}
