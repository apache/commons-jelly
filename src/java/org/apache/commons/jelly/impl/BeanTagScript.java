/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/impl/Attic/BeanTagScript.java,v 1.13 2002/08/01 09:53:17 jstrachan Exp $
 * $Revision: 1.13 $
 * $Date: 2002/08/01 09:53:17 $
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
 * $Id: BeanTagScript.java,v 1.13 2002/08/01 09:53:17 jstrachan Exp $
 */

package org.apache.commons.jelly.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;

import org.apache.commons.jelly.CompilableTag;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** <p><code>TagScript</code> evaluates a custom tag.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.13 $
  */

public class BeanTagScript extends TagScript {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(BeanTagScript.class);


// this state must be shared & thread safe...

    /** The cached Tag Class information */
    private Class tagClass;
    
    /** Expressions for each attribute */
    private Expression[] expressions = {
    };

    /** Write Methods for each attribute */
    private Method[] methods = {
    };

    /** Types of each attribute */
    private Class[] types = {
    };

    public BeanTagScript() {
    }

    public BeanTagScript(TagFactory tagFactory) {
        super(tagFactory);
    }

    // Script interface
    //-------------------------------------------------------------------------                

    /** Evaluates the body of a tag */
    public void run(JellyContext context, XMLOutput output) throws Exception {
        Tag tag = getTag();
        if ( tag == null ) {
            return;
        }
        tag.setContext(context);
        
        // initialize all the properties of the tag before its used
        // if there is a problem abort this tag
        for (int i = 0, size = expressions.length; i < size; i++) {
            Expression expression = expressions[i];
            Method method = methods[i];
            Class type = types[i];
            
            // some types are Expression objects so let the tag
            // evaluate them
            Object value = null;
            if (type.isAssignableFrom(Expression.class) && !type.isAssignableFrom(Object.class)) {
                value = expression;
            }
            else {
                value = expression.evaluate(context);
            }
            // convert value to correct type
            if (value != null) {
                value = convertType(value, type);
            }            
            Object[] arguments = { value };
            try {
                method.invoke(tag, arguments);
            }
            catch (Exception e) {
                String valueTypeName = (value != null ) ? value.getClass().getName() : "null";
                log.warn( 
                    "Cannot call method: " + method.getName() + " as I cannot convert: " 
                    + value + " of type: " + valueTypeName + " into type: " + type.getName() 
                );
                throw createJellyException( 
                    "Cannot call method: " + method.getName() + " on tag of type: " 
                    + tag.getClass().getName() + " with value: " + value + " of type: " 
                    + valueTypeName + ". Exception: " + e, e
                );
            }
        }
        
        try {
            tag.doTag(output);
        } 
        catch (JellyException e) {
            handleException(e);
        }
        catch (Exception e) {
            handleException(e);
        }
    }
    
    
    // Implementation methods
    //-------------------------------------------------------------------------                

    /**
     * Compiles a newly created tag if required, sets its parent and body.
     */
    protected void configureTag(Tag tag) throws Exception {
        super.configureTag(tag);
        Class aClass = tag.getClass();
        if ( tagClass == null || ! tagClass.equals( aClass ) ) {
            tagClass = aClass;
            compileClass();
        }
    }
    
    
    /** Compiles the given tag class caching the property descriptors etc.
      */
    protected void compileClass() throws Exception {
        List typeList = new ArrayList();
        List methodList = new ArrayList();
        List expressionList = new ArrayList();
        BeanInfo info = Introspector.getBeanInfo(tagClass);
        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        Set attributeSet = new HashSet();
        if (descriptors != null) {
            for (int i = 0, size = descriptors.length; i < size; i++) {
                PropertyDescriptor descriptor = descriptors[i];
                String name = descriptor.getName();
                Expression expression = (Expression) attributes.get(name);
                if (expression != null) {
                    attributeSet.add( name );
                    Method writeMethod = descriptor.getWriteMethod();
                    if (writeMethod != null) {
                        Class type = descriptor.getPropertyType();
                        expressionList.add(expression);
                        methodList.add(writeMethod);
                        typeList.add(type);
                        if (log.isDebugEnabled()) {
                            log.debug(
                                "Adding tag property name: "
                                    + name
                                    + " type: "
                                    + type.getName()
                                    + " expression: "
                                    + expression);
                        }
                    }
                }
            }
        }

        // System.err.println( "BeanTagScript::compile() " + this );
        
        // now create the arrays to avoid object allocation & casting when
        // running the script
        int size = expressionList.size();
        expressions = new Expression[size];
        methods = new Method[size];
        types = new Class[size];
        expressionList.toArray(expressions);
        methodList.toArray(methods);
        typeList.toArray(types);
        
        // now lets check for any attributes that are not used
        for ( Iterator iter = attributes.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            if ( ! attributeSet.contains( name ) ) {
                throw createJellyException( 
                    "This tag does not understand the attribute '" + name + "'"
                );
            }
        }
    }
    
}
