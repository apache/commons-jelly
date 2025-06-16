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
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathTagSupport;
import org.xml.sax.SAXException;

/**
 * A tag which outputs a DOCTYPE declaration to the current XML output pipe.
 * Note that there should only be a single DOCTYPE declaration in any XML stream and
 * it should occur before any element content.
 */
public class DoctypeTag extends XPathTagSupport {

    private String name;
    private String publicId;
    private String systemId;

    public DoctypeTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (name == null) {
            throw new MissingAttributeException( "name" );
        }

        try {
            output.startDTD(name, publicId, systemId);
            invokeBody(output);
            output.endDTD();
        } catch (final SAXException e) {
            throw new JellyTagException(e);
        }
    }

    // Properties
    //-------------------------------------------------------------------------
    /**
     * Returns the name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the publicId.
     * @return String
     */
    public String getPublicId() {
        return publicId;
    }

    /**
     * Returns the systemId.
     * @return String
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * Sets the document type name of the DOCTYPE
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the declared public identifier for DTD
     */
    public void setPublicId(final String publicId) {
        this.publicId = publicId;
    }

    /**
     * Sets the declared system identifier for the DTD
     */
    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }

}
