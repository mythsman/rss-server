package com.mythsman.server.enums;

/**
 * @author tusenpo
 * @date 6/5/21
 */
public enum FeedStatusEnum {
    NORMAL(0),
    NO_RSS(1),
    FORBIDDEN(2),
    STOPPED(3),
    ;
    int code;

    FeedStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
