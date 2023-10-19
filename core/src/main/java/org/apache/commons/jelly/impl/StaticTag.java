/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.impl;

import org.apache.commons.jelly.DynaTagSupport;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p><code>StaticTag</code> represents a static XML element
 * which echos itself to XMLOutput when it is invoked.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 */

public class StaticTag extends DynaTagSupport {

    /** The namespace URI */
    private String uri;

    /** The qualified name */
    private String qualifiedName;

    /** The local name */
    private String localName;

    /** The XML Attributes */
    private AttributesImpl attributes = new AttributesImpl();

    public StaticTag() {
    }

    public StaticTag(String uri, String localName, String qualifiedName) {
        this.uri = uri;
        this.localName = localName;
        this.qualifiedName = qualifiedName;
    }

    @Override
    public String toString() {
        return super.toString() + "[qname=" + qualifiedName + ";attributes=" + attributes + "]";
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        try {
            output.startElement(uri, localName, qualifiedName, attributes);
            invokeBody(output);
            output.endElement(uri, localName, qualifiedName);
        } catch (SAXException e) {
            throw new JellyTagException(e);
        } finally {
            attributes.clear();
        }
    }

    public void setAttribute(String name, String prefix, String nsURI, Object value) {
        if (value == null)
            return;
        if (prefix != null && prefix.length() > 0)
            attributes.addAttribute(nsURI, name, prefix + ":" + name, "CDATA", value.toString());
        else
            attributes.addAttribute("", name, name, "CDATA", value.toString());
    }

    // DynaTag interface
    //-------------------------------------------------------------------------
    @Override
    public void setAttribute(String name, Object value) throws JellyTagException {
        // ### we'll assume that all attributes are in no namespace!
        // ### this is severely limiting!
        // ### - Tag attributes should allow for namespace aware
        int index = attributes.getIndex("", name);
        if (index >= 0) {
            attributes.removeAttribute(index);
        }
        // treat null values as no attribute
        if (value != null) {
            attributes.addAttribute("", name, name, "CDATA", value.toString());
        }
    }

    // Properties
    //-------------------------------------------------------------------------
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getQName() {
        return qualifiedName;
    }

    public void setQName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        int idx = qualifiedName.indexOf(':');
        if (idx >= 0) {
            this.localName = qualifiedName.substring(idx + 1);
        }
        else {
            this.localName = qualifiedName;
        }
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
        // FIXME This just doesn't seem right or work...
        if (qualifiedName == null || !qualifiedName.endsWith(localName)) {
            localName = qualifiedName;
        }
    }
}
