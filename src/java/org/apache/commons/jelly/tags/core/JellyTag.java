/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/JellyTag.java,v 1.4 2002/05/15 06:25:46 jstrachan Exp $
 * $Revision: 1.4 $
 * $Date: 2002/05/15 06:25:46 $
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
 * $Id: JellyTag.java,v 1.4 2002/05/15 06:25:46 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.core;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.ScriptBlock;
import org.apache.commons.jelly.impl.TextScript;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** The root Jelly tag which should be evaluated first
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.4 $
  */
public class JellyTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog( JellyTag.class );

    /** whether whitespace should be trimmed or not. */
    private boolean trim = false;        

    /** Whether we've trimmed or not */
    private boolean hasTrimmed;
    
    public JellyTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void run(JellyContext context, XMLOutput output) throws Exception {
        if ( trim && ! hasTrimmed ) {
            trimBody();
            hasTrimmed = true;
        }
        if ( log.isDebugEnabled() ) {
            log.debug( "Running body: " + getBody() );
        }
        getBody().run( context, output );
    }

    // Properties
    //-------------------------------------------------------------------------                
    
    /** Sets whether whitespace should be trimmed or not. */
    public void setTrim(boolean trim) {
        this.trim = trim;
    }
    
    public void setBody(Script body) {
        super.setBody( body );
        hasTrimmed = false;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                
    
    /** 
     * Find all text nodes inside the top level of this body and 
     * if they are just whitespace then remove them
     */
    protected void trimBody() throws Exception {
        Script body = getBody();
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
    }
}
    
