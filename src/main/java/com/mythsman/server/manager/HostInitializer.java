package com.mythsman.server.manager;

import com.mythsman.server.enums.FeedTypeEnum;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author tusenpo
 * @date 6/5/21
 */
@Service
public class HostInitializer implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(HostInitializer.class);

    private OkHttpClient okHttpClient;

    @Override
    public void afterPropertiesSet() {

        okHttpClient = new OkHttpClient().newBuilder()
                .callTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))
                .followSslRedirects(true)
                .followRedirects(true)
                .connectTimeout(Duration.ofSeconds(1))
                .build();

    }

    /**
     * 根据 host 名，自动发现 feed 地址
     * 找不到则返回 null
     */
    public Pair<FeedTypeEnum, String> submit(String host) {
        //测试是http还是https。。
        Pair<FeedTypeEnum, String> pair = fetchFeedUrl("https://" + host);
        if (pair == null) {
            pair = fetchFeedUrl("http://" + host);
        }
        return pair;
    }

    /**
     * @param url 主站url
     * @return 该站的 feedUrl,feedType  链接，网站不存在则返 null
     */
    private Pair<FeedTypeEnum, String> fetchFeedUrl(String url) {
        Request request = new Request.Builder().url(url).get().build();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.code() != HttpStatus.OK.value()) {
                return Pair.of(FeedTypeEnum.UNKNOWN, "");
            }
            if (response.body() != null) {
                Document document = Jsoup.parse(response.body().string());
                Elements rssElement = document.select(new Evaluator.AttributeWithValue("type", "application/rss+xml"));
                String rssHref = rssElement.attr("href");
                if (StringUtils.isNotBlank(rssHref)) {
                    logger.info("feed path : {}", rssHref);
                    return Pair.of(FeedTypeEnum.RSS, rssHref);
                }
                Elements atomElement = document.select(new Evaluator.AttributeWithValue("type", "application/atom+xml"));
                String atomHref = atomElement.attr("href");
                if (StringUtils.isNotBlank(atomHref)) {
                    logger.info("feed path : {}", atomHref);
                    return Pair.of(FeedTypeEnum.ATOM, atomHref);
                }
            }
        } catch (Exception e) {
            logger.error("check url failed for {} ", url, e);
        }
        return null;
    }

}
