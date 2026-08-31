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

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.xml.secure.SecureSAXParserFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

public class ExpectedTag extends XMLUnitTagSupport {

    @Override
    protected SAXReader createSAXReader() {
        // dom4j builds its reader through JAXP internally; hand it one from the secure factory instead.
        try {
            return new SAXReader(SecureSAXParserFactory.newNSInstance().newSAXParser().getXMLReader());
        } catch (final ParserConfigurationException | SAXException e) {
            throw new IllegalStateException("Unable to create a new XML reader", e);
        }
    }

    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final Document expectedDocument = parseBody();

        final AssertDocumentsEqualTag assertTag =
            (AssertDocumentsEqualTag) findAncestorWithClass(AssertDocumentsEqualTag
                .class);
        assertTag.setExpected(expectedDocument);
    }

}
