package com.mythsman.server.enums;

/**
 * @author tusenpo
 * @date 6/5/21
 */
public enum FeedTypeEnum {
    RSS(0),
    ATOM(1),
    ;
    int code;

    FeedTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
