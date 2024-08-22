package com.demo.detailprocess.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseCMDB {

        /**
         * 操作结果
         */
        private Boolean result;

        /**
         * 错误码，result为True时，错误码为0
         */
        @JsonProperty("bk_error_code")
        private int bkErrorCode;

        /**
         * 错误信息
         */
        @JsonProperty("bk_error_msg")
        private String bkErrorMsg;

        /**
         * 数据
         */
        private Object data;
}
