package com.demo.detailprocess.common;

/**
 * 内部可检测到的错误信息
 */
public enum ErrorCode {
    DMS_APP_NO_EXIST_KEY("ERR1001", "应用不存在"),
    ERROR_ARG_KEY("ERR1002", "参数错误"),
    REFERENCED_BY_OTHER_MODEL("ERR1003", "模型被其他模型应用"),
    APP_HAS_MODELS("ERR1004", "应用下存在业务模型"),
    DMS_DATA_SOURCE_NO_EXIST_KEY("ERR1005", "数据源不存在"),
    DB_PROCESS_FAIL("ERR1006", "数据库处理错误"),
    XSS_ATTACK_EXISTS("ERR1007", "参数错误，请检查后重新输入正确的参数"),

    DUPLICATED_KEY("APS1001", "重复的KEY值"),
    FAILED_CALL_THIRD_PART("APS1002", "第三方调用失败"),
    NOT_EXISTED_KEY("APS1003", "KEY值不存在"),
    OPERATION_FAILED("APS1004", "操作失败"),
    PUBLISH_FAILED("APS1005", "发布失败"),
    ERROR_JSON_FORMAT("APS1006", "错误的JSON格式"),
    LACK_PRIMARY_KEY("APS1007", "缺少关键字段"),
    INVALID_MODEL_TYPE("APS1008", "错误的业务模型类型"),
    NO_EXECUTION_PERMISSION("APS1009", "没有Sql执行权限"),
    INVALID_SQL_STATEMENT("APS1010", "错误的Sql语句，仅支持以Select开头和;结尾的sql语句"),
    NOT_EXISTED_REFERENCE_TABLE("APS1011", "引用表不存在"),
    NOT_EXISTED_REFERENCE_COLUMN("APS1012", "引用列不存在"),
    FIXED_COLUMN_CANNOT_BE_UPDATED("APS1013", "数据模型固定列禁止修改"),


    //流程引擎
    PROC_UNKNOWEXCEPTION("PROC0000", "未知错误"),
    PROC_INS_QUERY_FAILED("PROC1011", "查询流程实例失败"),
    //adapter
    ERROR_TOKENMISS("ADA0000","token失效"),
    ERROR_USER_ID_EMPTY("ADA0001", "用户ID为空");


    private String code;
    private String desc;

    private ErrorCode(String code, String desc) {
        setCode(code);
        setDesc(desc);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "[" + this.code + "]" + this.desc;
    }
}
