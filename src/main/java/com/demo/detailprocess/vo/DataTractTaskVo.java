package com.demo.detailprocess.vo;


import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class DataTractTaskVo {
    private List<NodeVo> process;
    private BusinessVo creator;
    private String callBackUrl;
    private JSONObject nodesVar;

    public String check(){
        StringBuilder sb = new StringBuilder();
        boolean bool = true;
        for (NodeVo nodeVo : process) {
            int number = nodeVo.getNumber();
            if (StringUtils.isBlank(nodeVo.getName())) {
                sb.append("[ number = ").append(number).append(" ]").append(" name is null,");
                bool = false;
            }
            if (StringUtils.isBlank(nodeVo.getApprovalType())) {
                sb.append("[ number = ").append(number).append(" ]").append(" approvalType is null,");
                bool = false;
            }
            if (StringUtils.isBlank(nodeVo.getUserId())) {
                sb.append("[ number = ").append(number).append(" ]").append(" userId is null,");
                bool = false;
            }
            if (StringUtils.isBlank(nodeVo.getName())) {
                sb.append("[ number = ").append(number).append(" ]").append(" name is null,");
                bool = false;
            }
        }

        if (StringUtils.isBlank(creator.getBusinessType())) {
            sb.append("creator businessType is null,");
            bool = false;
        }
        if (StringUtils.isBlank(creator.getRelationId())) {
            sb.append("creator relationId is null,");
            bool = false;
        }
        if (StringUtils.isBlank(creator.getUserId())) {
            sb.append("creator userId is null,");
            bool = false;
        }

        if (StringUtils.isBlank(callBackUrl)) {
            sb.append("callBackUrl is null");
            bool = false;
        }
        if (!bool) {
            return sb.toString();
        }
        return null;
    }

    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
