package com.mythsman.server.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@SpringBootTest
class FeedServiceTest {

    @Autowired
    private FeedService feedService;

    @Test
    void submitHost() {
        feedService.submitHost("blog.mythsman.com");
    }
}