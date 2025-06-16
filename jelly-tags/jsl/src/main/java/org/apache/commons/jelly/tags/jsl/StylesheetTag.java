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
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathSource;
import org.apache.commons.jelly.xpath.XPathTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.rule.Rule;
import org.dom4j.rule.Stylesheet;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

/**
 * This tag implements a JSL stylesheet which is similar to an
 * XSLT stylesheet but can use Jelly tags inside it
 */
public class StylesheetTag extends XPathTagSupport implements XPathSource {

    /** The Log to which logging calls will be made. */
    private final Log log = LogFactory.getLog(StylesheetTag.class);

    /** Holds the stylesheet which will be applied to the source context. */
    private Stylesheet stylesheet;

    /** Holds value of property mode. */
    private String mode;

    /** The variable which the stylesheet will be output as */
    private String var;

    /** The XPath expression to evaluate. */
    private XPath select;

    /** The XPath source used by TemplateTag and ApplyTemplatesTag to pass XPath contexts */
    private Object xpathSource;

    public StylesheetTag() {
    }

    /**
     * Adds a new template rule to this stylesheet
     */
    public void addTemplate( final Rule rule ) {
        getStylesheet().addRule( rule );
    }

    /**
     * Factory method to create a new stylesheet
     */
    protected Stylesheet createStylesheet(final XMLOutput output) {
        final JellyStylesheet answer = new JellyStylesheet();
        answer.setOutput(output);
        return answer;
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        stylesheet = createStylesheet(output);

        // run the body to add the rules
        invokeBody(output);
        stylesheet.setModeName(getMode());

        if (var != null) {
            context.setVariable(var, stylesheet);
        }
        else {

            //dom4j seems to only throw generic Exceptions
            try {
                final Object source = getSource();

                if (log.isDebugEnabled()) {
                    log.debug("About to evaluate stylesheet on source: " + source);
                }

                stylesheet.run(source);
            }
            catch (final Exception e) {
                throw new JellyTagException(e);
            }

        }
    }

    // XPathSource interface
    //-------------------------------------------------------------------------

    /**
     * Getter for property mode.
     * @return Value of property mode.
     */
    public String getMode() {
        return mode;
    }

    /** @return the source on which the stylesheet should run
     */
    protected Object getSource() throws JaxenException {
        final Object source = getXPathContext();
        if ( select != null ) {
            return select.evaluate(source);
        }
        return source;
    }

    // Properties
    //-------------------------------------------------------------------------

    public Stylesheet getStylesheet() {
        return stylesheet;
    }

    /**
     * @return the XMLOutput from the stylesheet if available
     */
    public XMLOutput getStylesheetOutput() {
        if (stylesheet instanceof JellyStylesheet) {
            final JellyStylesheet jellyStyle = (JellyStylesheet) stylesheet;
            return jellyStyle.getOutput();
        }
        return null;
    }

    /**
     * @return the current XPath iteration value
     *  so that any other XPath aware child tags to use
     */
    @Override
    public Object getXPathSource() {
        return xpathSource;
    }

    /**
     * Sets the mode.
     * @param mode New value of property mode.
     */
    public void setMode(final String mode) {
        this.mode = mode;
    }

    /** Sets the XPath expression to evaluate. */
    public void setSelect(final XPath select) {
        this.select = select;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the XMLOutput to use by the current stylesheet
     */
    public void setStylesheetOutput(final XMLOutput output) {
        if (stylesheet instanceof JellyStylesheet) {
            final JellyStylesheet jellyStyle = (JellyStylesheet) stylesheet;
            jellyStyle.setOutput(output);
        }
    }

    /** Sets the variable name to define for this expression
     */
    public void setVar(final String var) {
        this.var = var;
    }

    /**
     * Sets the xpathSource.
     * @param xpathSource The xpathSource to set
     */
    void setXPathSource(final Object xpathSource) {
        this.xpathSource = xpathSource;
    }

}
