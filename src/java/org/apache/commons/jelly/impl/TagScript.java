/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/impl/TagScript.java,v 1.13 2002/06/27 14:09:15 jstrachan Exp $
 * $Revision: 1.13 $
 * $Date: 2002/06/27 14:09:15 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
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
 * $Id: TagScript.java,v 1.13 2002/06/27 14:09:15 jstrachan Exp $
 */
package org.apache.commons.jelly.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;

import org.apache.commons.jelly.CompilableTag;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.DynaTag;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Locator;

/** 
 * <p><code>TagScript</code> abstract base class for a 
 * script that evaluates a custom tag.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.13 $
 */
public abstract class TagScript implements Script {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TagScript.class);

    /** the tag to be evaluated */
    protected Tag tag;

    /** The attribute expressions that are created */
    protected Map attributes = new HashMap();
    
    /** the line number of the tag */
    private int lineNumber = -1;
    
    /** the column number of the tag */
    private int columnNumber = -1;

    public TagScript() {
    }

    public TagScript(Tag tag) {
        this.tag = tag;
    }
    
    public String toString() {
        return super.toString() + "[tag=" + tag + "]";
    }
    
    /**
     * Configures this TagScript from the SAX Locator, setting the column
     * and line numbers
     */
    public void setLocator(Locator locator) {
        setLineNumber( locator.getLineNumber() );
        setColumnNumber( locator.getColumnNumber() );
    }

    
    /** 
     * @return a new TagScript based on whether 
     * the tag is a bean tag or DynaTag 
     */
    public static TagScript newInstance(Tag tag) {
        if (tag instanceof DynaTag) {
            return new DynaTagScript((DynaTag) tag);
        }
        return new BeanTagScript(tag);
    }
    
    /** Add an initialization attribute for the tag.
     * This method must be called after the setTag() method 
     */
    public void addAttribute(String name, Expression expression) {
        if (log.isDebugEnabled()) {
            log.debug("adding attribute name: " + name + " expression: " + expression);
        }
        attributes.put(name, expression);
    }
    
    // Properties
    //-------------------------------------------------------------------------                
    /** @return the tag to be evaluated */
    public Tag getTag() {
        return tag;
    }
    
    /** Sets the tag to be evaluated */
    public void setTag(Tag tag) {
        this.tag = tag;
    }
    
    /** 
     * @return the line number of the tag 
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /** 
     * Sets the line number of the tag 
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /** 
     * @return the column number of the tag 
     */
    public int getColumnNumber() {
        return columnNumber;
    }
    
    /** 
     * Sets the column number of the tag 
     */
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------      
    
    /** 
     * Converts the given value to the required type. 
     *
     * @param value is the value to be converted. This will not be null
     * @param requiredType the type that the value should be converted to
     */
    protected Object convertType(Object value, Class requiredType)
        throws Exception {
        if (requiredType.isInstance(value)) {
            return value;
        }
        if (value instanceof String) {
            return ConvertUtils.convert((String) value, requiredType);
        }
        return value;
    }
    
    /**
     * A helper method to handle this non-Jelly exception.
     * This method will rethrow the exception, wrapped in a JellyException
     * while adding line number information etc.
     */
    protected void handleException(Exception e) throws Exception {
        log.error( "Caught exception: " + e, e );
        throw new JellyException(e, columnNumber, lineNumber);            
    }
    
    /**
     * A helper method to handle this Jelly exception.
     * This method adorns the JellyException with location information
     * such as adding line number information etc.
     */
    protected void handleException(JellyException e) throws Exception {
        if (e.getLineNumber() == -1) {
            e.setColumnNumber(columnNumber);
            e.setLineNumber(lineNumber);
        }
        throw e;
    }
}
