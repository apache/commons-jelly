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

import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathTagSupport;
import org.dom4j.Node;
import org.dom4j.io.SAXWriter;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.xml.sax.SAXException;

/** A tag which performs a copy-of operation like the XSLT tag
  */
public class CopyOfTag extends XPathTagSupport {

    /** The XPath expression to evaluate. */
    private XPath select;

    /** Should we output lexical XML data like comments
     * or entity names?
     */
    private boolean lexical;

    public CopyOfTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        final Object xpathContext = getXPathContext();

        if (select == null) {
            throw new MissingAttributeException( "select" );
        }

        SAXWriter saxWriter;

        if (lexical) {
            saxWriter = new SAXWriter(output, output);
        } else {
            saxWriter = new SAXWriter(output);
        }

        Object obj;
        try {
            obj = select.evaluate(xpathContext);
        } catch (final JaxenException e) {
            throw new JellyTagException("Failed to evaluate XPath expression", e);
        }

        try {
            if (obj instanceof List) {
                final List nodes = (List) obj;
                for (final Object object : nodes) {
                    if ( object instanceof Node ) {
                        saxWriter.write( (Node) object );
                    }
                    else if (object != null ) {
                        saxWriter.write( object.toString() );
                    }
                }
            } else if (obj instanceof Node) {
                saxWriter.write( (Node) obj );
            } else {
                saxWriter.write( obj.toString() );
            }
        } catch (final SAXException e) {
            throw new JellyTagException("Failed to write XML output.", e);
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
