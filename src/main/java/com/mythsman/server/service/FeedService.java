package com.mythsman.server.service;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.manager.FeedUpdater;
import com.mythsman.server.manager.HostInitializer;
import com.mythsman.server.repository.FeedRepository;
import com.mythsman.server.util.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
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
    public FeedEntity submitHost(String host) {
        FeedEntity entity = feedRepository.findByHost(host);
        if (entity != null) {
            return entity;
        }
        entity = hostInitializer.submit(host);
        if (entity != null && StringUtils.isNotBlank(entity.getTitle())) {
            entity.setHost(host);
            FeedEntity finalEntity = entity;
            CompletableFuture.runAsync(() -> {
                feedUpdater.updateFeed(finalEntity);
                finalEntity.setUuid(UUIDUtils.createUUID());
                feedRepository.save(finalEntity);
            });
            return entity;
        }
        return null;
    }

    public List<FeedEntity> queryByHost(List<String> hosts) {
        return feedRepository.findByHostIn(hosts);
    }

    public List<FeedEntity> queryAll() {
        return feedRepository.findAll();
    }
}
