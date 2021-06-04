package com.mythsman.server.controller;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.service.FeedService;
import com.mythsman.server.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@RestController
@RequestMapping("/feed")
public class FeedController {
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);
    private static final Pattern HOST_PATTERN = Pattern.compile("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$");
    @Autowired
    private FeedService feedService;

    @RequestMapping("/query_by_host")
    public FeedEntity queryByHost(@RequestParam("host") String host) {
        logger.info("query_by_host, host: {}", JsonUtils.toJson(host));
        return null;
    }

    @RequestMapping("/query_by_hosts")
    public List<FeedEntity> queryByHosts(@RequestParam("host") List<String> hosts) {
        logger.info("query_by_hosts, hosts: {}", JsonUtils.toJson(hosts));
        return Collections.emptyList();
    }

    @RequestMapping("/submit_host")
    public void submitHost(@RequestParam("host") String host) {
        logger.info("submit_host, host: {}", JsonUtils.toJson(host));
        if (HOST_PATTERN.matcher(host).matches()) {
            feedService.submitHost(host.toLowerCase().trim());
        }
    }

}
