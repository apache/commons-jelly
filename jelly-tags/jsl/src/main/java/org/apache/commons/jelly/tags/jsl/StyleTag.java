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
package org.apache.commons.jelly.tags.jsl;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.rule.Stylesheet;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

/**
 * This tag performs a JSL stylesheet which was previously
 * created via an &lt;stylesheet&gt; tag.
 */
public class StyleTag extends XPathTagSupport {

    /** The Log to which logging calls will be made. */
    private final Log log = LogFactory.getLog(StyleTag.class);

    /** Holds the stylesheet which will be applied to the source context. */
    private Stylesheet stylesheet;

    /** The XPath expression to evaluate. */
    private XPath select;

    public StyleTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        final Stylesheet stylesheet = getStylesheet();
        if (stylesheet == null) {
            throw new MissingAttributeException("stylesheet");
        }

        if (stylesheet instanceof JellyStylesheet) {
            final JellyStylesheet jellyStyle = (JellyStylesheet) stylesheet;
            jellyStyle.setOutput(output);
        }

        // dom4j only seems to throw Exception
        try {
            final Object source = getSource();
            if (log.isDebugEnabled()) {
                log.debug("About to evaluate stylesheet on source: " + source);
            }

            stylesheet.run(source);
        } catch (final Exception e) {
            throw new JellyTagException(e);
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /** @return the source on which the stylesheet should run
     */
    protected Object getSource() throws JaxenException {
        final Object source = getXPathContext();
        if ( select != null ) {
            return select.evaluate(source);
        }
        return source;
    }

    public Stylesheet getStylesheet() {
        return stylesheet;
    }

    /** Sets the XPath expression to evaluate. */
    public void setSelect(final XPath select) {
        this.select = select;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the stylesheet to use to style this tags body
     */
    public void setStylesheet(final Stylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }
}
