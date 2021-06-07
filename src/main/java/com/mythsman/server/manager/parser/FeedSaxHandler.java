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

    private final StringBuilder pathBuilder = new StringBuilder();

    private final StringBuilder characterBuilder = new StringBuilder();

    public FeedSaxHandler(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
        pathBuilder.append("/").append(qualifiedName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        characterBuilder.append(ch, start, length);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            String data = characterBuilder.toString().trim();
            if (pathBuilder.toString().startsWith("/rss")) {
                processRss(data);
            } else if (pathBuilder.toString().startsWith("/feed")) {
                processAtom(data);
            }
        } finally {
            pathBuilder.delete(pathBuilder.length() - qName.length() - 1, pathBuilder.length());
            characterBuilder.setLength(0);
        }
    }

    private void processRss(String data) {
        switch (pathBuilder.toString()) {
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
            case "/rss/channel/item":
                throw new SaxParseTerminated();
            default:
                return;
        }
        logger.info("{} rss {} -> {}", feedEntity.getHost(), pathBuilder, data);
    }


    private void processAtom(String data) {
        switch (pathBuilder.toString()) {
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
            case "/feed/entry":
                throw new SaxParseTerminated();
            default:
                return;
        }
        logger.info("{} atom {} -> {}", feedEntity.getHost(), pathBuilder, data);
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