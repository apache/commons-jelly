/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/bean/src/test/org/apache/commons/jelly/tags/bean/MyTagLibrary.java,v 1.3 2003/01/24 05:26:13 morgand Exp $
 * $Revision: 1.3 $
 * $Date: 2003/01/24 05:26:13 $
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
 *
 * $Id: MyTagLibrary.java,v 1.3 2003/01/24 05:26:13 morgand Exp $
 */
package org.apache.commons.jelly.tags.bean;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.impl.TagScript;

import org.xml.sax.Attributes;

/** 
 * A normal tag library which will use a BeanTag to create beans but this tag
 * library does not derive from BeanTagLibrary and so does not have a &lt;
 * beandef&gt; tag
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.3 $
 */
public class MyTagLibrary extends TagLibrary {

    public MyTagLibrary() {
    }
    
    
    // TagLibrary interface
    //-------------------------------------------------------------------------
    public TagScript createTagScript(String name, Attributes attributes) throws Exception {

        TagFactory factory = new TagFactory() {
            public Tag createTag(String name, Attributes attributes) throws JellyException {
                return createBeanTag(name, attributes);
            }
        };
        return new TagScript( factory );
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Factory method to create a Tag for the given tag and attributes. If this
     * tag matches a root bean, then a BeanTag will be created, otherwise a
     * BeanPropertyTag is created to make a nested property.
     */
    protected Tag createBeanTag(String name, Attributes attributes) throws JellyException {
        // is the name bound to a specific class
        Class beanType = getBeanType(name, attributes);
        if (beanType != null) {
            return new BeanTag(beanType, name);
        }

        // its a property tag
        return new BeanPropertyTag(name);
    }

    /**
     * Return the bean class that we should use for the given element name
     * 
     * @param name is the XML element name
     * @param attributes the XML attributes
     * @return Class the bean class to use for this element or null if the tag
     * is a nested property
     */
    protected Class getBeanType(String name, Attributes attributes) {
        if (name.equals( "customer")) {
            return Customer.class;
        }
        return null;
    }
}
