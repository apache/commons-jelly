/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/define/src/java/org/apache/commons/jelly/tags/define/ExtendTag.java,v 1.2 2003/01/26 00:07:23 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/26 00:07:23 $
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
 * $Id: ExtendTag.java,v 1.2 2003/01/26 00:07:23 morgand Exp $
 */
package org.apache.commons.jelly.tags.define;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.DynamicTagLibrary;

/** 
 * &lt;extend&gt; is used to extend a dynamic tag defined in an inherited 
 * dynamic tag library
 * <p/>
 *
 * @author <a href="mailto:tima@intalio.com">Tim Anderson</a>
 * @version $Revision: 1.2 $
 * @see SuperTag
 */
public class ExtendTag extends DefineTagSupport {
    
    private String name;

    private Script superScript;
    
    public ExtendTag() {
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws JellyTagException {
        DynamicTagLibrary library = getTagLibrary();
        DynamicTagLibrary owner = library.find(getName());
        if (owner == null) {
            throw new JellyTagException(
                "Cannot extend " + getName() + ": dynamic tag not defined");
        }
        if (owner == library) {
            // disallow extension of tags defined within the same tag
            // library
            throw new JellyTagException("Cannot extend " + getName() + 
                                     ": dynamic tag defined locally");
        }
        superScript = owner.getDynamicTag(name);
        if (superScript == null) {
            // tag doesn't define a script - disallow this for the moment.
            throw new JellyTagException("Cannot extend " + getName() + 
                                     ": tag is not a dynamic tag");
        }

        owner.registerDynamicTag(getName() , getBody());
    }    

    // Properties
    //-------------------------------------------------------------------------
    
    /** 
     * @return the name of the tag to create 
     */
    public String getName() {
        return name;
    }
    
    /** 
     * Sets the name of the tag to create 
     */
    public void setName(String name) {
        this.name = name;
    }    

    /**
     * Returns the parent implementation of this tag
     */
    public Script getSuperScript() {
        return superScript;
    }
}

