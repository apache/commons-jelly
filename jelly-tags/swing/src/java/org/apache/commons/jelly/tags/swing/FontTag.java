/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing;

import java.awt.Font;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MapTagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Creates an Font and attaches it to the parent component or exports the font as
 * a reusable variable that can be attached to multiple widgets. 
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class FontTag extends MapTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(FontTag.class);

    /** the current font instance */
    private Font font;

    public FontTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
/*    
 * maybe do some type conversions or name mapping code...
 * 
    public void setAttribute(String name, Object value) {
        if (name.equals("size")) {
            super.setAttribute(name, ConvertUtils.convert(Integer.class, value));
        }
        else {
            super.setAttribute(name, value);
        }
    }
*/    
    
    public void doTag(final XMLOutput output) throws JellyTagException {
        Map attributes = getAttributes();
        String var = (String) attributes.remove("var");
        
        font = createFont(attributes);

        if (var != null) {
            context.setVariable(var, font);
        }
        else {
            // now lets add this font to its parent if we have one
            ComponentTag tag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
            if ( tag != null ) {
                tag.setFont(font);
            }
            else {
                throw new JellyTagException( "this tag must be nested within a JellySwing widget tag or the 'var' attribute must be specified" );
            }
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * @return the Font object for this tag
     */
    public Font getFont() {
        return font;
    }

    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Factory method to create a new Font based on the given properties
     */
    protected Font createFont(Map map) {
        log.info( "Creating font from properties: " + map );
        Font font = new Font(map);
        //Font font = Font.getFont(map);
        log.info( "Created font: " + font );
        return font;
    }
}
