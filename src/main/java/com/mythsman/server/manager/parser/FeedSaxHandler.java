package com.mythsman.server.manager.parser;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.enums.FeedTypeEnum;
import com.mythsman.server.exceptions.SaxParseTerminated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FeedSaxHandler extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(FeedSaxHandler.class);

    private final FeedEntity feedEntity;

    private final StringBuilder path = new StringBuilder();

    public FeedSaxHandler(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
        path.append("/").append(qualifiedName);
        if (qualifiedName.equals("item")) {
            throw new SaxParseTerminated();
        }

        if (qualifiedName.equals("entry")) {
            throw new SaxParseTerminated();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String data = new String(ch, start, length);
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
                Date date = parseDate(data);
                feedEntity.setLastModified(date);
                break;
            default:
                break;
        }

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
                Date date = parseDate(data);
                feedEntity.setLastModified(date);
                break;
            default:
                break;
        }
        logger.info("{} -> {}", path, data);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        path.delete(path.length() - qName.length() - 1, path.length());
    }


    private Date parseDate(String text) {
        Date date = null;
        try {
            date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").parse(text);
            feedEntity.setLastModified(date);
        } catch (ParseException ignored) {
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(text);
                feedEntity.setLastModified(date);
            } catch (ParseException ignored) {
            }
        }
        if (date == null) {
            throw new RuntimeException("date format failed: " + text);
        }
        return date;
    }
}