package com.mythsman.server.advice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mythsman.server.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogFormatter {
    private List<Operation> operates;
    private Long time;

    @JsonIgnore
    private transient Long startTime;

    public LogFormatter() {
        reset();
    }

    public void reset() {
        time = 0L;
        startTime = 0L;
        operates = new ArrayList<>();
    }

    public void startLog() {
        reset();
        startTime = System.currentTimeMillis();
    }

    public void operateLog(String logMsg, Object... args) {
        operates.add(new Operation(logMsg, args));
    }

    public void operateLog(String logMsg, Map<String, Object> argMap) {
        operates.add(new Operation(logMsg, argMap));
    }

    public String endLog() {
        time = System.currentTimeMillis() - startTime;
        return JsonUtils.toJson(this);
    }

    public Long getTime() {
        return time;
    }
}
