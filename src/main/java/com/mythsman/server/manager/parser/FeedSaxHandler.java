package com.mythsman.server.manager.parser;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.enums.FeedTypeEnum;
import com.mythsman.server.exceptions.SaxParseTerminated;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FeedSaxHandler extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(FeedSaxHandler.class);

    private static final List<Pair<String, Integer>> dateTimePatterns = Arrays.asList(
            Pair.of("EEE, dd MMM yyyy HH:mm:ss zzz", 0),
            Pair.of("yyyy-MM-dd'T'HH:mm:ssXXX", 0),
            Pair.of("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Calendar.getInstance().get(Calendar.ZONE_OFFSET))
    );

    private final FeedEntity feedEntity;

    private final StringBuilder path = new StringBuilder();

    public FeedSaxHandler(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
        path.append("/").append(qualifiedName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String data = new String(ch, start, length).trim();
        if (path.toString().startsWith("/rss")) {
            switch (path.toString()) {
                case "/rss/channel/title":
                    feedEntity.setTitle(data);
                    feedEntity.setFeedType(FeedTypeEnum.RSS.getCode());
                    break;
                case "/rss/channel/description":
                    feedEntity.setSubTitle(data);
                    break;
                case "/rss/channel/generator":
                    feedEntity.setGenerator(data);
                    break;
                case "/rss/channel/lastBuildDate":
                    Date buildDate = parseDate(data);
                    feedEntity.setLastModified(buildDate);
                    break;
                case "/rss/channel/item/pubDate":
                    Date pubDate = parseDate(data);
                    feedEntity.setLastPublished(pubDate);
                    break;
                default:
                    break;
            }
            logger.info("rss {} -> {}", path, data);
        }

        if (path.toString().startsWith("/feed")) {

            switch (path.toString()) {
                case "/feed/title":
                    feedEntity.setTitle(data);
                    feedEntity.setFeedType(FeedTypeEnum.ATOM.getCode());
                    break;
                case "/feed/subtitle":
                    feedEntity.setSubTitle(data);
                    break;
                case "/feed/generator":
                    feedEntity.setGenerator(data);
                    break;
                case "/feed/updated":
                    Date updated = parseDate(data);
                    feedEntity.setLastModified(updated);
                    break;
                case "/feed/entry/published":
                    Date published = parseDate(data);
                    feedEntity.setLastPublished(published);
                    break;
                default:
                    break;
            }
            logger.info("atom {} -> {}", path, data);

        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        path.delete(path.length() - qName.length() - 1, path.length());
        if (qName.equals("item")) {
            throw new SaxParseTerminated();
        }

        if (qName.equals("entry")) {
            throw new SaxParseTerminated();
        }
    }

    private Date parseDate(String text) {
        for (Pair<String, Integer> patternPair : dateTimePatterns) {
            try {
                String pattern = patternPair.getLeft();
                Integer offset = patternPair.getRight();
                Date date = new SimpleDateFormat(pattern).parse(text);
                return new Date(date.getTime() + offset);
            } catch (ParseException ignored) {
            }
        }
        throw new RuntimeException("date format failed: " + text);
    }
}