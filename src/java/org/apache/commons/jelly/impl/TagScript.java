/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/impl/TagScript.java,v 1.10 2002/05/17 15:18:10 jstrachan Exp $
 * $Revision: 1.10 $
 * $Date: 2002/05/17 15:18:10 $
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
 * $Id: TagScript.java,v 1.10 2002/05/17 15:18:10 jstrachan Exp $
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
import org.apache.commons.jelly.DynaTag;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * <p><code>TagScript</code> abstract base class for a 
 * script that evaluates a custom tag.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.10 $
 */
public abstract class TagScript implements Script {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TagScript.class);

    /** the tag to be evaluated */
    protected Tag tag;

    /** The attribute expressions that are created */
    protected Map attributes = new HashMap();

    public TagScript() {
    }

    public TagScript(Tag tag) {
        this.tag = tag;
    }
    
    public String toString() {
        return super.toString() + "[tag=" + tag + "]";
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
    
    // Implementation methods
    //-------------------------------------------------------------------------      
    /** Converts the given value to the required type. 
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
}
