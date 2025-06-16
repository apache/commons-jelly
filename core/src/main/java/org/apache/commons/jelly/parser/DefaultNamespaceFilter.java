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
package org.apache.commons.jelly.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * XMLFilter that can provide a default namespace when
 * one has not been declared by the XML document.  Note:
 * this class does not address the namespace of attributes.
 */
public class DefaultNamespaceFilter extends XMLFilterImpl {

    protected String uriDefault = null;

    /**
     * Filter for undefined an undefined namespace
     *
     * @param defaultNamespace
     *               uri for the jelly namespace
     * @param reader XMLReader to filter
     */
    public DefaultNamespaceFilter(final String defaultNamespace, final XMLReader reader) {
        super(reader);
        this.uriDefault = defaultNamespace;
    }

    /**
     * All incoming empty URIs will be remapped to the default.
     *
     * @param namespaceURI
     *                  URI to check and potentially replace
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName)
            throws SAXException {
        if (namespaceURI.isEmpty()) {
            super.endElement(this.uriDefault, localName, qName);
        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }

    /**
     * All incoming empty URIs will be remapped to the default.
     *
     * @param uri       URI to check and potentially replace
     * @param localName
     * @param qName
     * @param atts
     * @throws SAXException
     */
    @Override
    public void startElement(final java.lang.String uri,
                             final java.lang.String localName,
                             final java.lang.String qName,
                             final Attributes atts)
            throws SAXException {

        if (uri.isEmpty()) {
            super.startElement(this.uriDefault, localName, qName, atts);
        } else {
            super.startElement(uri, localName, qName, atts);
        }

    }

    /**
     * All incoming empty URIs will be remapped to the default.
     *
     * @param prefix incoming prefix
     * @param uri    URI to check and potentially replace
     * @throws SAXException
     */
    @Override
    public void startPrefixMapping(final java.lang.String prefix,
                                   final java.lang.String uri)
    throws SAXException {

        if (uri.isEmpty()) {
            super.startPrefixMapping(prefix, this.uriDefault);
        } else {
            super.startPrefixMapping(prefix, uri);
        }
    }
}