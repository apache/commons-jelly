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
 */
package org.apache.commons.jelly.tags.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.MapTagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.BeanSource;

/** 
 * A tag which sets the bean properties on the given bean.
 * So if you used it as follows, for example using the &lt;j:new&gt; tag.
 * <pre>
 * &lt;j:new className="com.acme.Person" var="person"/&gt;
 * &lt;j:setProperties object="${person}" name="James" location="${loc}"/&gt;
 * </pre>
 * Then it would set the name and location properties on the bean denoted by
 * the expression ${person}.
 * <p>
 * This tag can also be nested inside a bean tag such as the &lt;useBean&gt; tag
 * or a JellySwing tag to set one or more properties, maybe inside some conditional
 * logic.
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.3 $
 */
public class SetPropertiesTag extends MapTagSupport  {

    public SetPropertiesTag(){
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {
        Map attributes = getAttributes();
        Object bean = attributes.remove( "object" );
        if ( bean == null ) {
            // lets try find a parent bean
            BeanSource tag = (BeanSource) findAncestorWithClass(BeanSource.class);
            if (tag != null) {
                bean = tag.getBean();
            }
            if (bean == null) {
                throw new MissingAttributeException("bean");
            }
        }
        setBeanProperties(bean, attributes);
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Sets the properties on the bean. Derived tags could implement some custom 
     * type conversion etc.
     */
    protected void setBeanProperties(Object bean, Map attributes) throws JellyException {
        try {
            BeanUtils.populate(bean, attributes);
        } catch (IllegalAccessException e) {
            throw new JellyException("could not set the properties on a bean",e);
        } catch (InvocationTargetException e) {
            throw new JellyException("could not set the properties on a bean",e);
        }
    }
}
