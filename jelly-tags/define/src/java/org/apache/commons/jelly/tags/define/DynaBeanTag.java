/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/define/src/java/org/apache/commons/jelly/tags/define/DynaBeanTag.java,v 1.3 2003/10/09 21:21:17 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 21:21:17 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * $Id: DynaBeanTag.java,v 1.3 2003/10/09 21:21:17 rdonkin Exp $
 */

package org.apache.commons.jelly.tags.define;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.Attribute;
import org.apache.commons.jelly.impl.DynamicDynaBeanTag;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/** 
 * Binds a Java bean to the given named Jelly tag so that the attributes of
 * the tag set the bean properties..
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.3 $
 */
public class DynaBeanTag extends DefineTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(DynaBeanTag.class);

    /** An empty Map as I think Collections.EMPTY_MAP is only JDK 1.3 onwards */
    private static final Map EMPTY_MAP = new HashMap();

    /** the name of the tag to create */
    private String name;
    
    /** the DyanClass to bind to the tag */
    private DynaClass dynaClass;

    /** the name of the attribute used for the variable name */
    private String varAttribute = "var";

    /** the attribute definitions for this dynamic tag */
    private Map attributes;
    
    /**
     * Adds a new attribute definition to this dynamic tag 
     */
    public void addAttribute(Attribute attribute) {
        if ( attributes == null ) {
            attributes = new HashMap();
        }
        attributes.put( attribute.getName(), attribute );
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        invokeBody(output);
        
		if (name == null) {
			throw new MissingAttributeException("name");
		}
		if (dynaClass == null) {
			throw new MissingAttributeException("dynaClass");
		}
        
        final DynaClass theDynaClass = dynaClass;
        final Map beanAttributes = (attributes != null) ? attributes : EMPTY_MAP;
        
        TagFactory factory = new TagFactory() {
            public Tag createTag(String name, Attributes attributes) {
                return  new DynamicDynaBeanTag(theDynaClass, beanAttributes, varAttribute);
            }
        };
        getTagLibrary().registerBeanTag(name, factory);
        
        // now lets clear the attributes for next invocation and help the GC
        attributes = null;
	}

    
    // Properties
    //-------------------------------------------------------------------------                    
    
    /** 
     * Sets the name of the tag to create
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the name of the attribute used to define the bean variable that this dynamic
     * tag will output its results as. This defaults to 'var' though this property
     * can be used to change this if it conflicts with a bean property called 'var'.
     */
    public void setVarAttribute(String varAttribute) {    
        this.varAttribute = varAttribute;
    }
        
    /**
     * Returns the dynaClass.
     * @return DynaClass
     */
    public DynaClass getDynaClass() {
        return dynaClass;
    }

    /**
     * Sets the {@link DynaClass} which will be bound to this dynamic tag.
     */
    public void setDynaClass(DynaClass dynaClass) {
        this.dynaClass = dynaClass;
    }

}
