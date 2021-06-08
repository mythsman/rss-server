package com.mythsman.server.advice;

import java.util.Map;

public class Operation {
    private String operateName;
    private Object[] operatePara;
    private Map<String, Object> operateParaMap;

    public Operation(String _operateName, Object... args) {
        this.operateName = _operateName;
        this.operatePara = args;
    }

    public Operation(String operateName, Map<String, Object> operateParaMap) {
        this.operateName = operateName;
        this.operateParaMap = operateParaMap;
    }

    public String getOperateName() {
        return operateName;
    }

    public Object[] getOperatePara() {
        return operatePara;
    }

    public Map<String, Object> getOperateParaMap() {
        return operateParaMap;
    }
}
