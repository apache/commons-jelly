/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.jelly.tags.jsl;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.xml.XPathSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Node;
import org.dom4j.rule.Action;
import org.dom4j.rule.Pattern;
import org.dom4j.rule.Rule;

/** 
 * This tag represents a declarative matching rule, similar to the template tag in XSLT.
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.8 $
 */
public class TemplateTag extends TagSupport implements XPathSource {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(TemplateTag.class);
    

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

    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        StylesheetTag tag = (StylesheetTag) findAncestorWithClass( StylesheetTag.class );
        if (tag == null) {
            throw new JellyException( "This <template> tag must be used inside a <stylesheet> tag" );
        }

        if ( log.isDebugEnabled() ) {        
            log.debug( "adding template rule for match: " + match );
        }
        
        Rule rule = createRule(tag, output);
        if ( rule != null && tag != null) {
            rule.setMode( mode );
            tag.addTemplate( rule );
        }
    }

    // XPathSource interface
    //-------------------------------------------------------------------------                    

    /**
     * @return the current XPath value on which relative paths are evaluated
     */
    public Object getXPathSource() {
        return xpathSource;
    }
    

    // Properties
    //-------------------------------------------------------------------------                

    public void setMatch(Pattern match) {
        this.match = match;
    }
    
    /** Getter for property priority.
     * @return Value of property priority.
     */
    public double getPriority() {
        return priority;
    }
    
    /** Sets the priority.
     * @param priority New value of property priority.
     */
    public void setPriority(double priority) {
        this.priority = priority;
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    
    /** Sets the name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /** Sets the mode.
     * @param mode New value of property mode.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    
    // Implementation methods
    //------------------------------------------------------------------------- 
    protected Rule createRule(StylesheetTag tag, XMLOutput output) {
        return new Rule( match, createAction(tag, output) );
    }
    
    protected Action createAction(final StylesheetTag tag, final XMLOutput output) {
        return new Action() {
            public void run(Node node) throws Exception {
                
                // store the context for use by applyTemplates tag
                tag.setXPathSource( node );
                
                xpathSource = node;
    
                if (log.isDebugEnabled()) {
                    log.debug( "Firing template body for match: " + match + " and node: " + node );          
                }
                invokeBody(output);
            }
        };                    
    }
}
