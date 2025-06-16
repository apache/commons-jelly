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
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.rule.Action;
import org.dom4j.rule.Pattern;
import org.dom4j.rule.Rule;

/**
 * This tag represents a declarative matching rule, similar to the template tag in XSLT.
 */
public class TemplateTag extends TagSupport implements XPathSource {

    /** The Log to which logging calls will be made. */
    private final Log log = LogFactory.getLog(TemplateTag.class);

    /** Holds value of property name. */
    private String name;

    /** Holds value of property mode. */
    private String mode;

    /** Holds value of property priority. */
    private double priority;

    /** The pattern to match */
    private Pattern match;

    /** The source XPath context for any child tags */
    private Object xpathSource;

    public TemplateTag() {
    }

    protected Action createAction(final StylesheetTag tag, final XMLOutput output) {
        return node -> {

            // store the context for use by applyTemplates tag
            tag.setXPathSource( node );

            xpathSource = node;

            if (log.isDebugEnabled()) {
                log.debug( "Firing template body for match: " + match + " and node: " + node );
            }

            XMLOutput actualOutput = tag.getStylesheetOutput();
            if (actualOutput == null) {
                actualOutput = output;
            }

            invokeBody(actualOutput);
        };
    }

    // XPathSource interface
    //-------------------------------------------------------------------------

    // Implementation methods
    //-------------------------------------------------------------------------
    protected Rule createRule(final StylesheetTag tag, final XMLOutput output) {
        return new Rule( match, createAction(tag, output) );
    }

    // Properties
    //-------------------------------------------------------------------------

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final StylesheetTag tag = (StylesheetTag) findAncestorWithClass( StylesheetTag.class );
        if (tag == null) {
            throw new JellyTagException( "This <template> tag must be used inside a <stylesheet> tag" );
        }

        if ( log.isDebugEnabled() ) {
            log.debug( "adding template rule for match: " + match );
        }

        final Rule rule = createRule(tag, output);
        if ( rule != null && tag != null) {
            rule.setMode( mode );
            tag.addTemplate( rule );
        }
    }

    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }

    /** Getter for property priority.
     * @return Value of property priority.
     */
    public double getPriority() {
        return priority;
    }

    /**
     * @return the current XPath value on which relative paths are evaluated
     */
    @Override
    public Object getXPathSource() {
        return xpathSource;
    }

    public void setMatch(final Pattern match) {
        this.match = match;
    }

    /** Sets the mode.
     * @param mode New value of property mode.
     */
    public void setMode(final String mode) {
        this.mode = mode;
    }

    /** Sets the name.
     * @param name New value of property name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /** Sets the priority.
     * @param priority New value of property priority.
     */
    public void setPriority(final double priority) {
        this.priority = priority;
    }
}
