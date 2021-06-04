package com.mythsman.server.util;

import java.util.UUID;

/**
 * @author tusenpo
 * @date 6/5/21
 */
public class UUIDUtils {

    public static String createUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }
}
