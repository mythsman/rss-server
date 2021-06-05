package com.mythsman.server.repository;

import com.mythsman.server.entity.FeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author tusenpo
 * @date 6/5/21
 */
public interface FeedRepository extends JpaRepository<FeedEntity, Long> {

    List<FeedEntity> findByLastCheckTimeBeforeAndStatusOrderByLastCheckTimeAsc(Date lastCheckTime, Integer status);

    FeedEntity findByHost(String host);

    List<FeedEntity> findByHostIn(Collection<String> host);
}
