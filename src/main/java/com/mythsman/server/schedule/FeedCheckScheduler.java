package com.mythsman.server.schedule;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.enums.FeedStatusEnum;
import com.mythsman.server.manager.FeedUpdater;
import com.mythsman.server.repository.FeedRepository;
import com.mythsman.server.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@Service
public class FeedCheckScheduler implements InitializingBean {
    private static final Long CHECK_INTERVAL_MILLIS = TimeUnit.DAYS.toMillis(1);
    private static final Logger logger = LoggerFactory.getLogger(FeedCheckScheduler.class);

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private FeedUpdater feedUpdater;

    private ExecutorService executorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = Executors.newFixedThreadPool(50);
    }

    @Scheduled(cron = "12 * * * * ?")
    public void schedule() {
        Date checkDate = new Date(System.currentTimeMillis() - CHECK_INTERVAL_MILLIS);
        List<FeedEntity> normalCandidates = feedRepository.findByLastCheckTimeBeforeAndStatusOrderByLastCheckTimeAsc(checkDate, FeedStatusEnum.NORMAL.getCode());
        List<FeedEntity> abnormalCandidates = feedRepository.findByLastCheckTimeBeforeAndStatusOrderByLastCheckTimeAsc(checkDate, FeedStatusEnum.NO_RSS.getCode());
        logger.info("normal candidates : {}", JsonUtils.toJson(normalCandidates));
        logger.info("abnormal candidates : {}", JsonUtils.toJson(abnormalCandidates));

        for (FeedEntity normalCandidate : normalCandidates) {
            executorService.submit(() -> handleFeed(normalCandidate));
        }

        for (FeedEntity abnormalCandidate : abnormalCandidates) {
            executorService.submit(() -> handleFeed(abnormalCandidate));
        }
    }

    private void handleFeed(FeedEntity feedEntity) {
        feedUpdater.updateFeed(feedEntity);
        feedEntity.setId(feedEntity.getId());
        feedEntity.setUuid(feedEntity.getUuid());
        feedEntity.setGmtCreate(feedEntity.getGmtCreate());
        feedRepository.save(feedEntity);
    }

}
