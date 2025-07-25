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
import org.jaxen.JaxenException;
import org.jaxen.XPath;

/**
 * Evaluates the XPath expression to be a boolean and only evaluates the body
 * if the expression is true.
 */
public class IfTag extends XPathTagSupport {

    /** The XPath expression to evaluate. */
    private XPath select;

    public IfTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (select == null) {
            throw new MissingAttributeException( "select" );
        }

        final Object xpathContext = getXPathContext();

        try {
            if ( select.booleanValueOf(xpathContext) ) {
                invokeBody(output);
            }
        } catch (final JaxenException e) {
            throw new JellyTagException(e);
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    // Implementation methods
    //-------------------------------------------------------------------------
    @Override
    protected Object getXPathContext() {
        final ForEachTag tag = (ForEachTag) findAncestorWithClass( ForEachTag.class );
        if ( tag != null ) {
            return tag.getXPathContext();
        }
        return null;
    }

    /** Sets the XPath expression to evaluate. */
    public void setSelect(final XPath select) {
        this.select = select;
    }

}
