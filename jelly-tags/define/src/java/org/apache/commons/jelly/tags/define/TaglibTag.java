/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/define/src/java/org/apache/commons/jelly/tags/define/TaglibTag.java,v 1.2 2003/01/26 00:07:23 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/26 00:07:23 $
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
 * $Id: TaglibTag.java,v 1.2 2003/01/26 00:07:23 morgand Exp $
 */
package org.apache.commons.jelly.tags.define;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.DynamicTagLibrary;

/** 
 * The &lt;taglib&gt; tag is used to define a new tag library
 * using a Jelly script..</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public class TaglibTag extends TagSupport {
    
    /** The namespace URI */
    private String uri;
    /** The new tags being added */
    private DynamicTagLibrary tagLibrary;
    /** Whether or not inheritence is enabled */
    private boolean inherit = true;
    
    public TaglibTag() {
    }
    
    public TaglibTag(String uri) {
        this.uri = uri;
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws JellyTagException {
        String uri = getUri();
        tagLibrary = new DynamicTagLibrary( uri );

        // inherit tags from an existing tag library
        if ( isInherit() ) {
            tagLibrary.setParent( context.getTagLibrary( uri ) );
        }
        context.registerTagLibrary( uri, tagLibrary );
        
        invokeBody(output);

        tagLibrary = null;
    }    
    
    // Properties
    //-------------------------------------------------------------------------                    
    public String getUri() {
        return uri;
    }

    /**
     * Sets the namespace URI to register this new dynamic tag library with
     */    
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public DynamicTagLibrary getTagLibrary() {
        return tagLibrary;
    }
    
    /**
     * Returns the inherit.
     * @return boolean
     */
    public boolean isInherit() {
        return inherit;
    }

    /**
     * Sets whether this dynamic tag should inherit from the current existing tag library 
     * of the same URI. This feature is enabled by default so that tags can easily be
     * some tags can be overridden in an existing library, such as when making Mock Tags.
     * 
     * You can disable this option if you want to disable any tags in the base library,
     * turning them into just normal static XML.
     *
     * @param inherit The inherit to set
     */
    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

}
