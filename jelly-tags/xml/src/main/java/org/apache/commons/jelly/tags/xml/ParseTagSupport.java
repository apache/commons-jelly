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
package org.apache.commons.jelly.tags.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

/**
 * An abstract base class for any tag which parsers its body as XML.
 */
public abstract class ParseTagSupport extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ParseTagSupport.class);

    /** The variable that will be generated for the document */
    private String var;

    /** The markup text to be parsed */
    private String text;

    /** The SAXReader used to parser the document */
    private SAXReader saxReader;

    public ParseTagSupport() {
    }

    /**
     * Factory method to create a new SAXReader
     */
    protected abstract SAXReader createSAXReader() throws SAXException;

    /** @return the SAXReader used for parsing, creating one lazily if need be  */
    public SAXReader getSAXReader() throws SAXException {
        if (saxReader == null) {
            saxReader = createSAXReader();
        }
        return saxReader;
    }

    /**
     * Returns the text to be parsed
     * @return String
     */
    public String getText() {
        return text;
    }

    // Properties
    //-------------------------------------------------------------------------
    /** The variable name that will be used for the Document variable created
     */
    public String getVar() {
        return var;
    }

    /**
     * Parses the given source
     */
    protected Document parse(Object source) throws JellyTagException {
        // #### we should allow parsing to output XML events to
        // the output if no var is specified

        try {
            if (source instanceof String) {
                final String uri = (String) source;
                source = context.getResource(uri);
            }

            if (source instanceof URL) {
                return getSAXReader().read((URL) source);
            }
            if (source instanceof File) {
                return getSAXReader().read((File) source);
            }
            if (source instanceof Reader) {
                return getSAXReader().read((Reader) source);
            }
            if (source instanceof InputStream) {
                return getSAXReader().read((InputStream) source);
            }
            throw new IllegalArgumentException(
                "Invalid source argument. Must be a String, Reader, InputStream or URL."
                    + " Was type; "
                    + source.getClass().getName()
                    + " with value: "
                    + source);
        }
        catch (final DocumentException | SAXException | MalformedURLException e) {
            throw new JellyTagException(e);
        }
    }

    /**
     * Parses the body of this tag and returns the parsed document
     */
    protected Document parseBody(final XMLOutput output) throws JellyTagException {
        final SAXContentHandler handler = new SAXContentHandler();
        final XMLOutput newOutput = new XMLOutput(handler);

        try {
            handler.startDocument();
            invokeBody( newOutput);
            handler.endDocument();
            return handler.getDocument();
        } catch (final SAXException e) {
            throw new JellyTagException(e);
        }
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Parses the give piece of text as being markup
     */
    protected Document parseText(final String text) throws JellyTagException {
        if ( log.isDebugEnabled() ) {
            log.debug( "About to parse: " + text );
        }

        try {
            return getSAXReader().read( new StringReader( text ) );
        }
        catch (final DocumentException | SAXException e) {
            throw new JellyTagException(e);
        }
    }

    /** Sets the SAXReader used for parsing */
    public void setSAXReader(final SAXReader saxReader) {
        this.saxReader = saxReader;
    }

    /**
     * Sets the text to be parsed by this parser
     * @param text The text to be parsed by this parser
     */
    public void setText(final String text) {
        this.text = text;
    }

    /** Sets the variable name that will be used for the Document variable created
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
