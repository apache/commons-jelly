/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/ant/SetPropertyTagSupport.java,v 1.4 2002/06/25 20:43:30 werken Exp $
 * $Revision: 1.4 $
 * $Date: 2002/06/25 20:43:30 $
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
 * $Id: SetPropertyTagSupport.java,v 1.4 2002/06/25 20:43:30 werken Exp $
 */

package org.apache.commons.jelly.tags.ant;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tag which sets an attribute on the parent Ant Task if the given value is not null.
 * This can be useful when setting parameters on Ant tasks, only if they have been specified
 * via some well defined property, otherwise allowing the inbuilt default to be used.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 */
public class SetPropertyTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(SetPropertyTag.class);

    private String name;
    private Object value;
    private Object defaultValue;
    
    public SetPropertyTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    public void doTag(XMLOutput output) throws Exception {
        if (name == null) {
            throw new MissingAttributeException("name");
        }
        TaskSource tag = (TaskSource) findAncestorWithClass( TaskSource.class );
        if ( tag == null ) {
            throw new JellyException( "This tag must be nested within an Ant task tag" );
        }
        Object value = getValue();
        if (value == null) {
            value = getDefault();
        }
        if (value != null) {
            tag.setTaskProperty(name, value);
        }
    }


    // Properties
    //-------------------------------------------------------------------------

    /**
     * Returns the name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value.
     * @return Object
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the name of the Ant task property to set.
     * @param name The name of the Ant task property to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the value of the Ant task property to set.
     * @param value The value of the Ant task property to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Returns the defaultValue.
     * @return Object
     */
    public Object getDefault() {
        return defaultValue;
    }

    /**
     * Sets the default value to be used if the specified value is empty.
     */
    public void setDefault(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

}
