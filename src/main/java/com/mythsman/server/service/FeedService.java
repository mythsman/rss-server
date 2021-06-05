package com.mythsman.server.service;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.manager.FeedUpdater;
import com.mythsman.server.manager.HostInitializer;
import com.mythsman.server.repository.FeedRepository;
import com.mythsman.server.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@Service
public class FeedService {
    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private HostInitializer hostInitializer;

    @Autowired
    private FeedUpdater feedUpdater;


    /**
     * @param host 站点域名
     * @return 站点的 feedUrl or null
     */
    public String submitHost(String host) {
        FeedEntity entity = feedRepository.findByHost(host);
        if (entity != null) {
            return entity.getHost();
        }
        String feedUrl = hostInitializer.submit(host);
        if (feedUrl != null) {
            CompletableFuture.runAsync(() -> {
                FeedEntity feedEntity = feedUpdater.updateFeed(host, feedUrl);
                if (feedEntity != null) {
                    feedEntity.setUuid(UUIDUtils.createUUID());
                    feedRepository.save(feedEntity);
                }
            });
            return feedUrl;
        }
        return null;
    }

    public List<FeedEntity> queryByHost(List<String> hosts) {
        return feedRepository.findByHostIn(hosts);
    }
}
