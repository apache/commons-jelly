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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/** A tag which parses some XML and defines a variable with the parsed Document.
  * The XML can either be specified as its body or can be passed in via the
  * XML property which can be a Reader, InputStream, URL or String URI.
  */
public class ParseTag extends ParseTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ParseTag.class);

    /** The XML to parse, either a String URI, a Reader or InputStream */
    private Object xml;

    // Optional properties not defined in JSTL
    /** Whether XML validation is enabled or disabled */
    private boolean validate;

    public ParseTag() {
    }

    /**
     * Factory method to create a new SAXReader
     */
    @Override
    protected SAXReader createSAXReader() {
        return new SAXReader(validate);
    }

    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (getVar() == null) {
            throw new MissingAttributeException("The var attribute cannot be null");
        }

        final Document document = getXmlDocument(output);
        context.setVariable(getVar(), document);
    }

    /** @return whether XML validation is enabled or disabled */
    public boolean getValidate() {
        return validate;
    }

    /** Gets the source of the XML which is either a String URI, Reader or InputStream */
    public Object getXml() {
        return this.xml;
    }

    protected Document getXmlDocument(final XMLOutput output) throws JellyTagException {
        Document document = null;
        final Object xmlObj = this.getXml();

        if (xmlObj == null) {
            final String text = getText();
            if (text != null) {
                document = parseText(text);
            }
            else {
                document = parseBody(output);
            }
        }
        else {
            document = parse(xmlObj);
        }

        return document;
    }

    /** Sets whether XML validation is enabled or disabled */
    public void setValidate(final boolean validate) {
        this.validate = validate;
    }

    /** Sets the source of the XML which is either a String URI, a File, Reader or InputStream */
    public void setXml(final Object xml) {
        this.xml = xml;
    }

}
