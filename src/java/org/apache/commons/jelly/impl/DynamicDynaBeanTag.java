/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;

import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.BeanSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This tag is bound onto a {@link DynaClass} instance. 
 * When the tag is invoked a {@link DynaBean will be created using the tags attributes. 
 * So this class is like a {@link DynaBean} implemenation of {@link DynamicBeanTag}
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class DynamicDynaBeanTag extends DynaBeanTagSupport implements BeanSource {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(DynamicDynaBeanTag.class);

    /** the bean class */
    private DynaClass beanClass;
    
    /** 
     * the tag attribute name that is used to declare the name 
     * of the variable to export after running this tag
     */
    private String variableNameAttribute;

    /** the current variable name that the bean should be exported as */
    private String var;

    /** the set of attribute names we've already set */
    private Set setAttributesSet = new HashSet();

    /** the attribute definitions */
    private Map attributes;    
        
    public DynamicDynaBeanTag(DynaClass beanClass, Map attributes, String variableNameAttribute) {
        this.beanClass = beanClass;
        this.attributes = attributes;
        this.variableNameAttribute = variableNameAttribute;
    }

    public void beforeSetAttributes() throws Exception {
        // create a new dynabean before the attributes are set
        setDynaBean( beanClass.newInstance() );

        setAttributesSet.clear();                    
    }

    public void setAttribute(String name, Object value) throws Exception {        
        boolean isVariableName = false;
        if (variableNameAttribute != null ) {
            if ( variableNameAttribute.equals( name ) ) {
                if (value == null) {
                    var = null;
                }
                else {
                    var = value.toString();
                }
                isVariableName = true;
            }
        }
        if (! isVariableName) {
            
            // #### strictly speaking we could
            // know what attributes are specified at compile time
            // so this dynamic set is unnecessary            
            setAttributesSet.add(name);
            
            // we could maybe implement attribute specific validation here
            
            super.setAttribute(name, value);
        }
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {

        // lets find any attributes that are not set and 
        for ( Iterator iter = attributes.values().iterator(); iter.hasNext(); ) {
            Attribute attribute = (Attribute) iter.next();
            String name = attribute.getName();
            if ( ! setAttributesSet.contains( name ) ) {
                if ( attribute.isRequired() ) {
                    throw new MissingAttributeException(name);
                }
                // lets get the default value
                Object value = null;
                Expression expression = attribute.getDefaultValue();
                if ( expression != null ) {
                    value = expression.evaluate(context);
                }
                
                // only set non-null values?
                if ( value != null ) {
                    super.setAttribute(name, value);
                }
            }
        }

        invokeBody(output);

        // export the bean if required
        if ( var != null ) {
            context.setVariable(var, getDynaBean());
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                    
    /**
     * @return the bean that has just been created 
     */
    public Object getBean() {
        return getDynaBean();
    }
}
