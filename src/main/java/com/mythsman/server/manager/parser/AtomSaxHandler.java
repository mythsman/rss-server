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

public class AtomSaxHandler extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(AtomSaxHandler.class);

    private final FeedEntity feedEntity;

    private final StringBuilder path = new StringBuilder();

    public AtomSaxHandler(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
        path.append("/").append(qualifiedName);
        if (qualifiedName.equals("entry")) {
            throw new SaxParseTerminated();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String data = new String(ch, start, length);
        switch (path.toString()) {
            case "/feed/title":
                feedEntity.setTitle(data);
                break;
            case "/feed/subtitle":
                feedEntity.setSubTitle(data);
                break;
            case "/feed/generator":
                feedEntity.setGenerator(data);
                break;
            case "/feed/updated":
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