package com.demo.detailprocess.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO {
    public ResponseVO(ResponseCMDB responseCMDB) {
        setResult(responseCMDB.getResult());
        if (!result) {
            setErrorCode("APS1002");
            setErrorMsg(responseCMDB.getBkErrorMsg());
        }
        setData(responseCMDB.getData());
    }
    public static ResponseVO failed(ErrorCode errorCode) {
        return ResponseVO.builder().result(false).errorCode(errorCode.getCode()).errorMsg(errorCode.getDesc()).build();
    }

    public static ResponseVO failed(String errorMsg) {
        return ResponseVO.builder().result(false).errorCode(ErrorCode.OPERATION_FAILED.getCode()).errorMsg(errorMsg).build();
    }

    public static ResponseVO failed(String errorCode, String errorMsg) {
        return ResponseVO.builder().result(false).errorCode(errorCode).errorMsg(errorMsg).build();
    }

    public static ResponseVO success() {
        return ResponseVO.builder().result(true).data("SUCCESS").build();
    }

    public static ResponseVO success(Object data) {
        return ResponseVO.builder().result(true).data(data).build();
    }

    public static ResponseVO success(ErrorCode errorCode,String warningMessage,Object data){
        return ResponseVO.builder().result(true).errorCode(errorCode.getCode()).errorMsg(warningMessage).data(data).build();
    }

    /**
     * 结果
     */
    private Boolean result;
    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错信信息
     */
    private String errorMsg;

    /**
     * 当前页
     */
    private String curPage;

    /**
     * 页面大小
     */
    private String pageSize;


    /**
     * 数据总数
     */
    private String totalCount;

    /**
     * 页面总数
     */
    private String totalPages;

    /**
     * 数据
     */
    private Object data;
}
