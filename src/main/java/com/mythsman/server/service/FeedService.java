package com.mythsman.server.service;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.enums.FeedStatusEnum;
import com.mythsman.server.enums.FeedTypeEnum;
import com.mythsman.server.manager.FeedUpdater;
import com.mythsman.server.manager.HostInitializer;
import com.mythsman.server.repository.FeedRepository;
import com.mythsman.server.util.JsonUtils;
import com.mythsman.server.util.UUIDUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@Service
public class FeedService {
    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    private static final Long CHECK_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(1);

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private HostInitializer hostInitializer;

    @Autowired
    private FeedUpdater feedUpdater;

    @PostConstruct
    @Scheduled(cron = "12 * * * * ?")
    public void schedule() {
        Date checkDate = new Date(System.currentTimeMillis() - CHECK_INTERVAL_MILLIS);
        List<FeedEntity> normalCandidates = feedRepository.findByLastCheckTimeBeforeAndStatusOrderByLastCheckTimeAsc(checkDate, FeedStatusEnum.NORMAL.getCode());
        List<FeedEntity> abnormalCandidates = feedRepository.findByLastCheckTimeBeforeAndStatusOrderByLastCheckTimeAsc(checkDate, FeedStatusEnum.ABNORMAL.getCode());
        logger.info("normal candidates : {}", JsonUtils.toJson(normalCandidates));
        logger.info("abnormal candidates : {}", JsonUtils.toJson(abnormalCandidates));
    }


    /**
     * @param host 站点域名
     * @return 站点的 feedUrl or null
     */
    public String submitHost(String host) {
        Pair<FeedTypeEnum, String> pair = hostInitializer.submit(host);
        if (pair != null) {
            FeedEntity feedEntity = feedUpdater.updateFeed(host, pair.getRight(), pair.getLeft());
            if (feedEntity != null) {
                feedEntity.setUuid(UUIDUtils.createUUID());
                feedEntity.setLastCheckTime(new Date());
                feedRepository.save(feedEntity);
            }
            return pair.getRight();
        }
        return null;
    }

    public List<FeedEntity> queryByHost(List<String> hosts) {
        return feedRepository.findByHostIn(hosts);
    }
}
