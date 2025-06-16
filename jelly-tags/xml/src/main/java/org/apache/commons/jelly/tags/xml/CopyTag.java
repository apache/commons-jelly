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
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathTagSupport;
import org.dom4j.Element;
import org.dom4j.io.SAXWriter;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.xml.sax.SAXException;

/**
 * A tag which performs a copy operation like the XSLT tag,
 * performing a shallow copy of the element and its attributes but no content.
 */
public class CopyTag extends XPathTagSupport {

    /** The XPath expression to evaluate. */
    private XPath select;

    /** Should we output lexical XML data like entity names?
     */
    private boolean lexical;

    public CopyTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final Object xpathContext = getXPathContext();

        Object node = xpathContext;

        try {
            if (select != null) {
                node = select.selectSingleNode(xpathContext);
            }

            if ( node instanceof Element ) {
                final Element element = (Element) node;

                SAXWriter saxWriter;

                if (lexical) {
                    saxWriter = new SAXWriter(output, output);
                } else {
                    saxWriter = new SAXWriter(output);
                }

                saxWriter.writeOpen(element);
                invokeBody(output);
                saxWriter.writeClose(element);
            }
            else {
                invokeBody(output);
            }
        }
        catch (final SAXException | JaxenException e) {
            throw new JellyTagException(e);
        }
    }

    public void setLexical(final boolean lexical) {
        this.lexical = lexical;
    }
    // Properties
    //-------------------------------------------------------------------------
    /** Sets the XPath expression to evaluate. */
    public void setSelect(final XPath select) {
        this.select = select;
    }
}
