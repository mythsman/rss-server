package com.mythsman.server.controller;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.schedule.FeedCheckScheduler;
import com.mythsman.server.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@RestController
@RequestMapping("/feed")
public class FeedController {
    private static final Pattern HOST_PATTERN = Pattern.compile("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$");

    @Autowired
    private FeedService feedService;

    @Autowired
    private FeedCheckScheduler feedCheckScheduler;

    @RequestMapping("/query_by_host")
    public List<FeedEntity> queryByHost(@RequestParam("host") List<String> hosts) {
        return feedService.queryByHost(hosts);
    }

    @RequestMapping("/query_all")
    public List<FeedEntity> queryAll() {
        return feedService.queryAll();
    }

    @RequestMapping("/force_refresh")
    public void forceRefresh() {
        feedCheckScheduler.update(new Date());
    }

    @RequestMapping("/submit_host")
    public List<FeedEntity> submitHost(@RequestParam("host") List<String> hosts) {
        List<FeedEntity> list = new ArrayList<>();
        for (String host : hosts) {
            if (HOST_PATTERN.matcher(host).matches()) {
                FeedEntity entity = feedService.submitHost(host.toLowerCase().trim());
                if (entity != null) {
                    list.add(entity);
                }
            }
        }
        return list;
    }
}
