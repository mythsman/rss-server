package com.mythsman.server.enums;

/**
 * @author tusenpo
 * @date 6/5/21
 */
public enum FeedTypeEnum {
    UNKNOWN(0),
    RSS(1),
    ATOM(2),
    ;
    int code;

    FeedTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
