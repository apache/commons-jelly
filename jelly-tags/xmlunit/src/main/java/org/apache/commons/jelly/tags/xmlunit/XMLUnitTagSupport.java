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

package org.apache.commons.jelly.tags.xmlunit;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.junit.AssertTagSupport;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

public abstract class XMLUnitTagSupport extends AssertTagSupport {

    /** The SAXReader used to parser the document */
    private SAXReader saxReader;

    /**
     * Factory method to create a new SAXReader
     */
    protected abstract SAXReader createSAXReader();

    /** @return the SAXReader used for parsing, creating one lazily if need be  */
    public SAXReader getSAXReader() {
        if (saxReader == null) {
            saxReader = createSAXReader();
        }
        return saxReader;
    }

    /**
     * Parses the given source
     */
    protected Document parse(final Object source) throws JellyTagException {
        try {
            if (source instanceof Document) {
                return (Document) source;
            }
            if (source instanceof String) {
                final String uri = (String) source;
                final InputStream in = context.getResourceAsStream(uri);
                return getSAXReader().read(in, uri);
            }
            if (source instanceof Reader) {
                return getSAXReader().read((Reader) source);
            }
            if (source instanceof InputStream) {
                return getSAXReader().read((InputStream) source);
            }
            if (source instanceof URL) {
                return getSAXReader().read((URL) source);
            }
            throw new IllegalArgumentException(
                "Invalid source argument. Must be a Document, String, Reader, InputStream or URL."
                    + " Was type: "
                    + source.getClass().getName()
                    + " with value: "
                    + source);
        }
        catch (final DocumentException e) {
            throw new JellyTagException(e);
        }
    }

    /**
     * Parses the body of this tag and returns the parsed document
     */
    protected Document parseBody() throws JellyTagException {
        final SAXContentHandler handler = new SAXContentHandler();
        final XMLOutput newOutput = new XMLOutput(handler);
        try {
            handler.startDocument();
            invokeBody(newOutput);
            handler.endDocument();
        }
        catch (final SAXException e) {
            throw new JellyTagException(e);
        }
        return handler.getDocument();
    }

    /** Sets the SAXReader used for parsing */
    public void setSAXReader(final SAXReader saxReader) {
        this.saxReader = saxReader;
    }
}
