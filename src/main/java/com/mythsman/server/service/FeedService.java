package com.mythsman.server.service;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.enums.FeedStatusEnum;
import com.mythsman.server.enums.FeedTypeEnum;
import com.mythsman.server.repository.FeedRepository;
import com.mythsman.server.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@Service
public class FeedService {
    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    @Autowired
    private FeedRepository feedRepository;

    public void submitHost(String host) {
        FeedEntity feedEntity = new FeedEntity();
        feedEntity.setUuid(UUIDUtils.createUUID());
        feedEntity.setFeedType(FeedTypeEnum.RSS.getCode());
        feedEntity.setUseSsl(true);
        feedEntity.setRssPath("/rss/");
        feedEntity.setLastModified(new Date());
        feedEntity.setGenerator("ghost");
        feedEntity.setHost(host);
        feedEntity.setTitle("wow");
        feedEntity.setSubTitle("sub wow");
        feedEntity.setStatus(FeedStatusEnum.FORBIDDEN.getCode());
        feedEntity.setLastCheckTime(new Date());
        feedRepository.save(feedEntity);
    }

}
