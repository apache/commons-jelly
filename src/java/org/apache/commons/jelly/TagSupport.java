/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/TagSupport.java,v 1.22 2003/01/24 19:03:24 morgand Exp $
 * $Revision: 1.22 $
 * $Date: 2003/01/24 19:03:24 $
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
 * $Id: TagSupport.java,v 1.22 2003/01/24 19:03:24 morgand Exp $
 */
package org.apache.commons.jelly;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.impl.CompositeTextScriptBlock;
import org.apache.commons.jelly.impl.ScriptBlock;
import org.apache.commons.jelly.impl.TextScript;

/** <p><code>TagSupport</code> an abstract base class which is useful to 
  * inherit from if developing your own tag.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.22 $
  */

public abstract class TagSupport implements Tag {
    
    /** the parent of this tag */
    protected Tag parent;

    /** the body of the tag */  
    protected Script body;
    /** The current context */

    protected Boolean shouldTrim;
    protected boolean hasTrimmed;
    
    protected JellyContext context;

    /** 
     * Searches up the parent hierarchy from the given tag 
     * for a Tag of the given type 
     *
     * @param from the tag to start searching from
     * @param tagClass the type of the tag to find
     * @return the tag of the given type or null if it could not be found
     */
    public static Tag findAncestorWithClass(Tag from, Class tagClass) {
        // we could implement this as 
        //  return findAncestorWithClass(from,Collections.singleton(tagClass));
        // but this is so simple let's save the object creation for now
        while (from != null) {
            if (tagClass.isInstance(from)) {
                return from;
            }
            from = from.getParent();
        }
        return null;
    }

    /** 
     * Searches up the parent hierarchy from the given tag 
     * for a Tag matching one or more of given types.
     *
     * @param from the tag to start searching from
     * @param tagClasses a Collection of Class types that might match
     * @return the tag of the given type or null if it could not be found
     */
    public static Tag findAncestorWithClass(Tag from, Collection tagClasses) {
        while (from != null) {
            for(Iterator iter = tagClasses.iterator();iter.hasNext();) {
                Class klass = (Class)(iter.next());
                if (klass.isInstance(from)) {
                    return from;
                }
            }
            from = from.getParent();
        }
        return null;        
    }

    /** 
     * Searches up the parent hierarchy from the given tag 
     * for a Tag matching one or more of given types.
     *
     * @param from the tag to start searching from
     * @param tagClasses an array of types that might match
     * @return the tag of the given type or null if it could not be found
     * @see #findAncestorWithClass(Tag,Collection)
     */
    public static Tag findAncestorWithClass(Tag from, Class[] tagClasses) {
        return findAncestorWithClass(from,Arrays.asList(tagClasses));
    }
    
    public TagSupport() {
    }

    public TagSupport(boolean shouldTrim) {
        setTrim( shouldTrim );
    }

    /**
     * Sets whether whitespace inside this tag should be trimmed or not. 
     * Defaults to true so whitespace is trimmed
     */
    public void setTrim(boolean shouldTrim) {
        if ( shouldTrim ) {
            this.shouldTrim = Boolean.TRUE;
        } 
        else {
            this.shouldTrim = Boolean.FALSE;
        }
    }

    public boolean isTrim() {
        if ( this.shouldTrim == null ) {
            Tag parent = getParent();
            if ( parent == null ) {
                return true;
            } 
            else {
                if ( parent instanceof TagSupport ) {
                    TagSupport parentSupport = (TagSupport) parent;

                    this.shouldTrim = ( parentSupport.isTrim() ? Boolean.TRUE : Boolean.FALSE );
                } 
                else {
                    this.shouldTrim = Boolean.TRUE;
                }
            }
        }

        return this.shouldTrim.booleanValue();
    }
    
    /** @return the parent of this tag */
    public Tag getParent() {
        return parent;
    }
    
    /** Sets the parent of this tag */
    public void setParent(Tag parent) {
        this.parent = parent;
    }
    
    /** @return the body of the tag */
    public Script getBody() {
        if (! hasTrimmed) {
            hasTrimmed = true;
            if (isTrim()) {
                trimBody();
            }
        }
        return body;
    }
    
    /** Sets the body of the tag */
    public void setBody(Script body) {
        this.body = body;
        this.hasTrimmed = false;
    }
    
    /** @return the context in which the tag will be run */
    public JellyContext getContext() {
        return context;
    }
    
    /** Sets the context in which the tag will be run */
    public void setContext(JellyContext context) throws JellyException {
        this.context = context;
    }    
    
    /**
     * Invokes the body of this tag using the given output
     */
    public void invokeBody(XMLOutput output) throws JellyTagException {
        getBody().run(context, output);
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                
    /** 
     * Searches up the parent hierarchy for a Tag of the given type.
     * @return the tag of the given type or null if it could not be found
     */
    protected Tag findAncestorWithClass(Class parentClass) {
        return findAncestorWithClass(getParent(), parentClass);
    }
    
    /** 
     * Searches up the parent hierarchy for a Tag of one of the given types. 
     * @return the tag of the given type or null if it could not be found
     * @see #findAncestorWithClass(Collection)
     */
    protected Tag findAncestorWithClass(Class[] parentClasses) {
        return findAncestorWithClass(getParent(),parentClasses);
    }

    /** 
     * Searches up the parent hierarchy for a Tag of one of the given types. 
     * @return the tag of the given type or null if it could not be found
     */
    protected Tag findAncestorWithClass(Collection parentClasses) {
        return findAncestorWithClass(getParent(),parentClasses);
    }
    
    /**
     * Evaluates the given body using a buffer and returns the String 
     * of the result.
     *
     * @return the text evaluation of the body
     */
    protected String getBodyText() throws JellyException {
        StringWriter writer = new StringWriter();
        invokeBody(XMLOutput.createXMLOutput(writer));
        return writer.toString();
    }

    /**
     * Evaluates the given body using a buffer and returns the String 
     * of the result.
     *
     * @param shouldEscape Signal if the text should be escaped.
     *
     * @return the text evaluation of the body
     */
    protected String getBodyText(boolean shouldEscape) throws JellyException {
        StringWriter writer = new StringWriter();
        try {
          invokeBody(XMLOutput.createXMLOutput(writer,shouldEscape));
        } catch (UnsupportedEncodingException e) {
            throw new JellyException(e.toString());
        }
        return writer.toString();
    }


    /** 
     * Find all text nodes inside the top level of this body and 
     * if they are just whitespace then remove them
     */
    protected void trimBody() { 
        
        // #### should refactor this code into
        // #### trimWhitespace() methods on the Script objects
        
        if ( body instanceof CompositeTextScriptBlock ) {
            CompositeTextScriptBlock block = (CompositeTextScriptBlock) body;
            List list = block.getScriptList();
            int size = list.size();
            if ( size > 0 ) {
                Script script = (Script) list.get(0);
                if ( script instanceof TextScript ) {
                    TextScript textScript = (TextScript) script;
                    textScript.trimStartWhitespace();
                }
                if ( size > 1 ) {
                    script = (Script) list.get(size - 1);
	                if ( script instanceof TextScript ) {
	                    TextScript textScript = (TextScript) script;
	                    textScript.trimEndWhitespace();
	                }
                }
            }
        }
        else
        if ( body instanceof ScriptBlock ) {
            ScriptBlock block = (ScriptBlock) body;
            List list = block.getScriptList();
            for ( int i = list.size() - 1; i >= 0; i-- ) {
                Script script = (Script) list.get(i);
                if ( script instanceof TextScript ) {
                    TextScript textScript = (TextScript) script;
                    String text = textScript.getText();
                    text = text.trim();
                    if ( text.length() == 0 ) {
                        list.remove(i);
                    }
                    else {
                        textScript.setText(text);
                    }
                }
            }                
        }
        else if ( body instanceof TextScript ) {
            TextScript textScript = (TextScript) body;
            textScript.trimWhitespace();
        }
    }
}
