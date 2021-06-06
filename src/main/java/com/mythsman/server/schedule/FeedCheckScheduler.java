package com.mythsman.server.schedule;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.enums.FeedStatusEnum;
import com.mythsman.server.manager.FeedUpdater;
import com.mythsman.server.repository.FeedRepository;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
        update(checkDate);
    }

    /**
     * @param checkDate 检测最近更新时间在此之前的feed
     */
    public void update(Date checkDate) {
        List<FeedEntity> normalCandidates = feedRepository.findByLastCheckTimeBeforeAndStatusOrderByLastCheckTimeAsc(checkDate, FeedStatusEnum.NORMAL.getCode());
        List<FeedEntity> abnormalCandidates = feedRepository.findByLastCheckTimeBeforeAndStatusOrderByLastCheckTimeAsc(checkDate, FeedStatusEnum.NO_RSS.getCode());

        StopWatch stopWatch = StopWatch.createStarted();
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (FeedEntity normalCandidate : normalCandidates) {
            futures.add(CompletableFuture.runAsync(() -> handleFeed(normalCandidate), executorService));
        }

        for (FeedEntity abnormalCandidate : abnormalCandidates) {
            futures.add(CompletableFuture.runAsync(() -> handleFeed(abnormalCandidate)));
        }

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFuture.join();

        stopWatch.stop();

        logger.info("feed update schedule done in {} ms ", stopWatch.getTime());
    }

    private void handleFeed(FeedEntity feedEntity) {
        feedUpdater.updateFeed(feedEntity);
        feedEntity.setId(feedEntity.getId());
        feedEntity.setUuid(feedEntity.getUuid());
        feedEntity.setGmtCreate(feedEntity.getGmtCreate());
        feedRepository.save(feedEntity);
    }

}
