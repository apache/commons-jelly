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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */
package org.apache.commons.jelly.tags.jsl;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.rule.Stylesheet;
import org.jaxen.XPath;

/** 
 * Implements the apply templates function in the stylesheet, similar to the XSLT equivalent.
 * a JSP include.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.8 $
 */
public class ApplyTemplatesTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(ApplyTemplatesTag.class);
    
    /** Holds value of property mode. */
    private String mode;    

    /** Holds the XPath object */
    private XPath select;
    

    public ApplyTemplatesTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    /** By default just evaluate the body */
    public void doTag(XMLOutput output) throws JellyTagException {
        StylesheetTag tag = (StylesheetTag) findAncestorWithClass( StylesheetTag.class );
        if (tag == null) {
            throw new JellyTagException( 
                "<applyTemplates> tag must be inside a <stylesheet> tag"
            );
        }
        Stylesheet stylesheet = tag.getStylesheet();
        
        XMLOutput oldOutput = tag.getStylesheetOutput();
        tag.setStylesheetOutput(output);
        
        Object source = tag.getXPathSource();
        // for some reason, these DOM4J methods only throw Exception
        try {
            if ( select != null ) {
                stylesheet.applyTemplates( source, select );
            }
            else {
                stylesheet.applyTemplates( source );
            }
        } 
        catch (Exception e) {
            throw new JellyTagException(e);
        }
        
        tag.setStylesheetOutput(oldOutput);
        
        // #### should support MODE!!!
        
    }

    // Properties
    //-------------------------------------------------------------------------                

    public void setSelect( XPath select ) {
        this.select = select;
    }
    
    /** Sets the mode.
     * @param mode New value of property mode.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }    
}
