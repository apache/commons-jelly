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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Replace namespace is a filter to change the namespace of any
 * element attribute passing through it.
 *
 */
public class ReplaceNamespaceTag extends TagSupport {
    private String fromNamespace;
    private String toNamespace;

    public ReplaceNamespaceTag() {
    }

    //  Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        final String fromURI = fromNamespace != null ? fromNamespace : "";
        final String toURI = toNamespace != null ? toNamespace : "";
        XMLOutput newOutput = output;

        if (!toURI.equals(fromURI)) {
            newOutput = new XMLOutput(output) {
                @Override
                public void endElement(final String uri, final String localName, final String qName)
                    throws SAXException {
                    super.endElement(replaceURI(uri), localName, qName);
                }

                private Attributes replaceURI(final Attributes atts) {
                    final AttributesImpl newAttsImpl = new AttributesImpl();

                    for (int i = 0; i < atts.getLength(); i++) {
                        // Normally attributes don't have namespaces
                        // But may have (only if on form prefix:attr) ?
                        // So, we'll only replace if needed
                        final String QName = atts.getQName(i);
                        String newUri = atts.getURI(i);
                        final int idx = QName.indexOf(':');

                        if (idx >= 0) {
                            newUri = replaceURI(newUri);
                        }

                        newAttsImpl.addAttribute(newUri, atts.getLocalName(i), atts.getQName(i),
                            atts.getType(i), atts.getValue(i));
                    }

                    return newAttsImpl;
                }

                private String replaceURI(final String uri) {
                    String newUri = uri;

                    if (fromURI.equals(uri != null ? uri : "")) {
                        newUri = toURI;
                    }

                    return newUri;
                }

                @Override
                public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
                    throws SAXException {
                    super.startElement(replaceURI(uri), localName, qName, replaceURI(atts));
                }

                @Override
                public void startPrefixMapping(final String prefix, final String uri)
                    throws SAXException {
                    super.startPrefixMapping(prefix, replaceURI(uri));
                }
            };
        }

        invokeBody(newOutput);
    }

    /**
     * @return the source namespace URI to replace
     */
    public String getFromURI() {
        return fromNamespace;
    }

    /**
     * @return the destination namespace URI to replace
     */
    public String getToURI() {
        return toNamespace;
    }

    /**
     * Sets the source namespace URI to replace.
     */
    public void setFromURI(final String namespace) {
        this.fromNamespace = namespace;
    }

    /**
     * Sets the destination namespace URI to replace.
     */
    public void setToURI(final String namespace) {
        this.toNamespace = namespace;
    }
}