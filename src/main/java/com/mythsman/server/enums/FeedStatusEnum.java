package com.mythsman.server.enums;

/**
 * @author tusenpo
 * @date 6/5/21
 */
public enum FeedStatusEnum {
    NORMAL(0),
    ABNORMAL(1),
    STOPPED(2),
    ;
    int code;

    FeedStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
