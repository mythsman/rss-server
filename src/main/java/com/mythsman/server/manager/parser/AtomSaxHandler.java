package com.mythsman.server.manager.parser;

import com.mythsman.server.entity.FeedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AtomSaxHandler extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(AtomSaxHandler.class);

    private FeedEntity feedEntity;

    public AtomSaxHandler(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
        super.startElement(namespaceURI, localName, qualifiedName, attributes);
        logger.info("start {}, {}, {}", namespaceURI, localName, qualifiedName);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        super.endElement(namespaceURI, localName, qName);
        logger.info("end {}, {}, {}, ", namespaceURI, localName, qName);
    }

}