/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.util;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Ensures that only one start and end document event is passed onto the underlying
 * ContentHandler. This object can only be used once and then discarded.
 */
public class SafeContentHandler implements ContentHandler {
    private final ContentHandler handler;
    private boolean documentStarted;
    private boolean documentEnded;

    public SafeContentHandler(final ContentHandler handler) {
        this.handler = handler;
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void characters(final char[] arg0, final int arg1, final int arg2)
        throws SAXException {
        handler.characters(arg0, arg1, arg2);
    }

    /**
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void endDocument() throws SAXException {
        if (! documentEnded) {
            handler.endDocument();
            documentEnded = true;
        }
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void endElement(final String arg0, final String arg1, final String arg2)
        throws SAXException {
        handler.endElement(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void endPrefixMapping(final String arg0) throws SAXException {
        handler.endPrefixMapping(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void ignorableWhitespace(final char[] arg0, final int arg1, final int arg2)
        throws SAXException {
        handler.ignorableWhitespace(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void processingInstruction(final String arg0, final String arg1)
        throws SAXException {
        handler.processingInstruction(arg0, arg1);
    }

    /**
     * @param arg0
     */
    @Override
    public void setDocumentLocator(final Locator arg0) {
        handler.setDocumentLocator(arg0);
    }

    /**
     * @param arg0
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void skippedEntity(final String arg0) throws SAXException {
        handler.skippedEntity(arg0);
    }

    /**
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void startDocument() throws SAXException {
        if (! documentStarted) {
            handler.startDocument();
            documentStarted = true;
        }
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void startElement(
        final String arg0,
        final String arg1,
        final String arg2,
        final Attributes arg3)
        throws SAXException {
        handler.startElement(arg0, arg1, arg2, arg3);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void startPrefixMapping(final String arg0, final String arg1)
        throws SAXException {
        handler.startPrefixMapping(arg0, arg1);
    }
}
