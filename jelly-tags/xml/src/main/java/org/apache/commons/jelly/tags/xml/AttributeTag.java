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

/** Adds an XML attribute to the parent element tag like
  * the {@code &lt;xsl:attribute&gt;} tag.
  */
public class AttributeTag extends TagSupport {
     /** The namespace URI. */
    private String namespace;

    /** The name of the attribute. */
    private String name;

    public AttributeTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final ElementTag tag = (ElementTag) findAncestorWithClass( ElementTag.class );
        if (tag == null) {
            throw new JellyTagException(
                    "<attribute> tag must be enclosed inside an <element> tag" );
        }
        tag.setAttributeValue(getName(), getBodyText(false), getURI());
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the name of the attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the namespace URI of the element
     */
    public String getURI() {
        return namespace;
    }

    /**
     * Sets the name of the attribute.
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
