/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTagLibrary.java,v 1.6 2002/08/01 09:53:18 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2002/08/01 09:53:18 $
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
 * $Id: DynamicTagLibrary.java,v 1.6 2002/08/01 09:53:18 jstrachan Exp $
 */
package org.apache.commons.jelly.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.xml.sax.Attributes;

/** 
 * <p><code>DynamicTagLibrary</code> represents a TagLibrary which
 * gets created by running a Jelly script.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.6 $
 */
public class DynamicTagLibrary extends TagLibrary {

    private String uri;
    private Map templates = new HashMap();
    private TagLibrary parent;

    public DynamicTagLibrary() {
    }

    public DynamicTagLibrary(String uri) {
        this.uri = uri;
    }

    /** Creates a new script to execute the given tag name and attributes */
    public TagScript createTagScript(final String name, final Attributes attributes)
        throws JellyException {

        return new TagScript(
            new TagFactory() {
                public Tag createTag(String name, Attributes attributes) throws JellyException {
                    return DynamicTagLibrary.this.createTag(name, attributes);
                }
            }
        );
    }

    /** Creates a new Tag for the given tag name if it exists */
    public Tag createTag(String name, Attributes attributes)
        throws JellyException {

        Object value = templates.get(name);
        if ( value instanceof Script ) {            
            Script template = (Script) value;
            return new DynamicTag(template);
        }
        else if ( value instanceof TagFactory ) {
            TagFactory factory = (TagFactory) value;
            return factory.createTag(name, attributes);
        } 
        else if ( parent != null ) {
            // delegate to the parent
            return parent.createTag(name, attributes);
        }

        return null;
    }

    /**
     * Creates a new tag with the given name and template 
     */
    public void registerDynamicTag(String name, Script template) {
        templates.put(name, template);
    }

    /**
     * Creates a new Jelly Bean Tag with the given name 
     */
    public void registerBeanTag(String name, TagFactory factory) {
        templates.put(name, factory);
    }

    /**
     * Returns the script associated with the given tag name
     *
     * @param name The tag name
     * @return The script associated with <code>name</code>, or 
     *         <code>null</code> if the tag doesn't exist or isn't a script
     */
    public Script getDynamicTag(String name) {
        Object result = templates.get(name);
        return (result instanceof Script) ? (Script) result : null;
    }

    /**
     * Returns the tag library instance which contains the named tag.
     * <p/>
     * If the tag is not registered within this library, the set of
     * parent libraries will be searched.
     * 
     * @param name The tag name
     * @return The tag library containing the named tag, or <code>null</code>
     *         if the tag is not registered.
     */
    public DynamicTagLibrary find(String name) {
        DynamicTagLibrary result = null;
        if (templates.get(name) != null) {
            result = this;
        } 
        else if (parent instanceof DynamicTagLibrary) {
            result = ((DynamicTagLibrary) parent).find(name);
        }
        return result;
    }

    // Properties
    //-------------------------------------------------------------------------     
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    /**
     * Returns the parent library which will be used to resolve unknown tags.
     * @return TagLibrary
     */
    public TagLibrary getParent() {
        return parent;
    }

    /**
     * Sets the parent to inherit tags from that are not defined in this library.
     * @param parent The parent to set
     */
    public void setParent(TagLibrary parent) {
        this.parent = parent;
    }

}
