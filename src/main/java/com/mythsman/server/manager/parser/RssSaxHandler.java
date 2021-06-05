package com.mythsman.server.manager.parser;

import com.mythsman.server.entity.FeedEntity;
import com.mythsman.server.exceptions.SaxParseTerminated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RssSaxHandler extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(RssSaxHandler.class);


    private final FeedEntity feedEntity;

    private final StringBuilder path = new StringBuilder();

    public RssSaxHandler(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
        path.append("/").append(qualifiedName);
        if (qualifiedName.equals("item")) {
            throw new SaxParseTerminated();
        }
    }

    @Override
    public void characters(char[] ch, int start, int end) throws SAXException {
        String data = new String(ch, start, end);
        switch (path.toString()) {
            case "/rss/channel/title":
                feedEntity.setTitle(data);
                break;
            case "/rss/channel/description":
                feedEntity.setSubTitle(data);
                break;
            case "/rss/channel/generator":
                feedEntity.setGenerator(data);
                break;
            case "/rss/channel/lastBuildDate":
                try {
                    Date date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").parse(data);
                    feedEntity.setLastModified(date);
                } catch (ParseException e) {
                    logger.error("parse date failed,", e);
                }
                break;
        }
        logger.info("{} -> {}", path, data);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        path.delete(path.length() - qName.length() - 1, path.length());
    }

}