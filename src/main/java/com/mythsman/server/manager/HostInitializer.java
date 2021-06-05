package com.mythsman.server.manager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.regex.Pattern;

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
                .callTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(10))
                .followSslRedirects(true)
                .followRedirects(true)
                .connectTimeout(Duration.ofSeconds(1))
                .build();

    }

    /**
     * 根据 host 名，自动发现 feed 地址
     * 找不到则返回 null
     */
    public String submit(String host) {
        //测试是http还是https。。
        String feedUrl = fetchFeedUrl("https://" + host);
        if (feedUrl == null) {
            feedUrl = fetchFeedUrl("http://" + host);
        }
        return feedUrl;
    }

    /**
     * @param url 主站url
     * @return 该站的 feedUrl  链接，没有feedUrl返回空字符串，网站不存在则返 null
     */
    private String fetchFeedUrl(String url) {
        Request request = new Request.Builder().url(url).get().build();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.code() != HttpStatus.OK.value()) {
                return "";
            }
            if (response.body() != null) {

                Document document = Jsoup.parse(response.body().string());
                Elements feedElement = document.select(new Evaluator.AttributeWithValueMatching("type", Pattern.compile("application/(rss|atom)\\+xml")));
                String feedHref = feedElement.attr("href");
                if (StringUtils.isNotBlank(feedHref)) {
                    if (!feedHref.startsWith("http")) {
                        feedHref = Paths.get(response.request().url().toString(), feedHref).toString();
                    }
                    logger.info("feed path : {}", feedHref);
                    return feedHref;
                }
            }
        } catch (Exception e) {
            logger.error("check url failed for {} ", url, e);
        }
        return null;
    }

}
