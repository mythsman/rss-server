package com.mythsman.server.manager;

import com.mythsman.server.enums.FeedTypeEnum;
import org.junit.jupiter.api.Test;

/**
 * @author tusenpo
 * @date 6/5/21
 */
class FeedUpdaterTest {

    @Test
    void updateFeed() {
        FeedUpdater feedUpdater = new FeedUpdater();
        feedUpdater.afterPropertiesSet();
        feedUpdater.updateFeed("blog.mythsman.com", "https://blog.mythsman.com/rss/", FeedTypeEnum.RSS);
    }
}