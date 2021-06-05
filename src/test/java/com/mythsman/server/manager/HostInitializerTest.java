package com.mythsman.server.manager;

import org.junit.jupiter.api.Test;

/**
 * @author tusenpo
 * @date 6/5/21
 */
class HostInitializerTest {

    @Test
    void submit() {
        HostInitializer hostInitializer = new HostInitializer();
        hostInitializer.afterPropertiesSet();
        hostInitializer.submit("www.liaoxuefeng.com");
    }
}