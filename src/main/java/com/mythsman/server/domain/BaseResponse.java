package com.mythsman.server.domain;

import java.io.Serializable;

/**
 * @author tusenpo
 * @date 6/5/21
 */
public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 4739219742933299843L;

    private Boolean success;
    private String errorMsg;
    private Object result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
