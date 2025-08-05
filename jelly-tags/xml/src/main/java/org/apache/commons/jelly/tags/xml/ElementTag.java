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
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/** A tag to produce an XML element which can contain other attributes
  * or elements like the {@code &lt;xsl:element&gt;} tag.
  */
public class ElementTag extends TagSupport {

    /** The namespace URI. */
    private String namespace;

    /** The qualified name. */
    private String name;

    /** The XML Attributes. */
    private final AttributesImpl attributes = new AttributesImpl();

    /** Flag set if attributes are output. */
    private boolean outputAttributes;

    public ElementTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final int idx = name.indexOf(':');
        final String localName = idx >= 0
            ? name.substring(idx + 1)
            : name;

        outputAttributes = false;

        final XMLOutput newOutput = new XMLOutput(output) {

            // add an initialize hook to the core content-generating methods

            @Override
            public void characters(final char[] ch, final int start, final int length) throws SAXException {
                initialize();
                super.characters(ch, start, length);
            }

            @Override
            public void endElement(final String uri, final String localName, final String qName)
                throws SAXException {
                initialize();
                super.endElement(uri, localName, qName);
            }

            @Override
            public void ignorableWhitespace(final char[] ch, final int start, final int length)
                throws SAXException {
                initialize();
                super.ignorableWhitespace(ch, start, length);
            }

            /**
             * Ensure that the outer start element is generated
             * before any content is output.
             */
            protected void initialize() throws SAXException {
                if (!outputAttributes) {
                    super.startElement(namespace, localName, name, attributes);
                    outputAttributes = true;
                }
            }

            @Override
            public void objectData(final Object object)
                throws SAXException {
                initialize();
                super.objectData(object);
            }

            @Override
            public void processingInstruction(final String target, final String data)
                throws SAXException {
                initialize();
                super.processingInstruction(target, data);
            }

            @Override
            public void startElement(
                final String uri,
                final String localName,
                final String qName,
                final Attributes atts)
                throws SAXException {
                initialize();
                super.startElement(uri, localName, qName, atts);
            }
        };

        invokeBody(newOutput);

        try {
            if (!outputAttributes) {
                output.startElement(namespace, localName, name, attributes);
                outputAttributes = true;
            }

            output.endElement(namespace, localName, name);
            attributes.clear();
        } catch (final SAXException e) {
            throw new JellyTagException(e);
        }
    }

    /**
     * @return the qualified name of the element
     */
    public String getName() {
        return name;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the namespace URI of the element
     */
    public String getURI() {
        return namespace;
    }

    /**
     * Sets the attribute of the given name to the specified value.
     *
     * @param name of the attribute
     * @param value of the attribute
     * @param uri namespace of the attribute
     * @throws JellyTagException if the start element has already been output.
     *   Attributes must be set on the outer element before any content
     *   (child elements or text) is output
     */
    public void setAttributeValue(final String name, final String value, final String uri) throws JellyTagException {
        if (outputAttributes) {
            throw new JellyTagException(
                "Cannot set the value of attribute: "
                + name + " as we have already output the startElement() SAX event"
            );
        }

        // ### we'll assume that all attributes are in no namespace!
        // ### this is severely limiting!
        // ### we should be namespace aware
        // NAMESPACE FIXED:
        final int idx = name.indexOf(':');
        final String localName = idx >= 0
            ? name.substring(idx + 1)
            : name;
        final String nsUri = uri != null
            ? uri
            : "";

        final int index = attributes.getIndex(nsUri, localName);
        if (index >= 0) {
            attributes.removeAttribute(index);
        }
        // treat null values as no attribute
        if (value != null) {
            attributes.addAttribute(nsUri, localName, name, "CDATA", value);
        }
    }

    /**
     * Sets the qualified name of the element
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the namespace URI of the element
     */
    public void setURI(final String namespace) {
        this.namespace = namespace;
    }
}
