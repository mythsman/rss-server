package com.mythsman.server.manager;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.enums.FeedStatusEnum;
import com.mythsman.server.enums.FeedTypeEnum;
import com.mythsman.server.exceptions.SaxParseTerminated;
import com.mythsman.server.manager.parser.FeedSaxHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.time.Duration;
import java.util.Date;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@Service
public class FeedUpdater implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(FeedUpdater.class);

    private OkHttpClient okHttpClient;

    private SAXParserFactory saxParserFactory;

    @Override
    public void afterPropertiesSet() {
        saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setValidating(false);

        okHttpClient = new OkHttpClient().newBuilder()
                .callTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(10))
                .followSslRedirects(true)
                .followRedirects(true)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void updateFeed(FeedEntity feedEntity) {
        feedEntity.setLastCheckTime(new Date());
        if (StringUtils.isBlank(feedEntity.getFeedPath())) {
            feedEntity.setFeedType(FeedTypeEnum.UNKNOWN.getCode());
            feedEntity.setStatus(FeedStatusEnum.ABNORMAL.getCode());
            return;
        }
        Request request = new Request.Builder().url(feedEntity.getFeedPath()).get().build();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.code() != HttpStatus.OK.value()) {
                feedEntity.setFeedType(FeedTypeEnum.UNKNOWN.getCode());
                feedEntity.setStatus(FeedStatusEnum.ABNORMAL.getCode());
            } else if (response.body() != null) {
                InputStream inputStream = response.body().byteStream();
                SAXParser saxParser = saxParserFactory.newSAXParser();
                try {
                    saxParser.parse(inputStream, new FeedSaxHandler(feedEntity));
                } catch (SaxParseTerminated ignored) {
                }
                feedEntity.setStatus(FeedStatusEnum.NORMAL.getCode());
            }
        } catch (Exception e) {
            feedEntity.setStatus(FeedStatusEnum.ABNORMAL.getCode());
            logger.error("update feed failed for {} ", feedEntity.getFeedPath(), e);
        }
    }

}
