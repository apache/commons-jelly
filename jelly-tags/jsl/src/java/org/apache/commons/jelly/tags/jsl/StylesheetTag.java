/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */
package org.apache.commons.jelly.tags.jsl;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;

import org.dom4j.Node;
//import org.dom4j.XPath;
import org.dom4j.rule.Action;
import org.dom4j.rule.Rule;
import org.dom4j.rule.Stylesheet;

/** 
 * This tag implements a JSL stylesheet which is similar to an 
 * XSLT stylesheet but can use Jelly tags inside it. a JSP include.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.8 $
 */
public class StylesheetTag extends JSLTagSupport {

    /** Holds the stylesheet which will be applied to the source context. */
    private Stylesheet stylesheet = new Stylesheet();
    
    /** Holds value of property mode. */
    private String mode;    

    /** store the current output instance for use by inner classes */
    private XMLOutput output;

    /** the source on which the stylesheet will operate */
    private Object source;
    
    /** The variable which the stylesheet will be output as */
    private String var;
        
    public StylesheetTag() {
        // add default actions
        stylesheet.setValueOfAction( 
            new Action() {
                public void run(Node node) throws Exception {
                    String text = node.getStringValue();
                    if ( text != null && text.length() > 0 ) {
                        output.write( text );
                    }
                }
            }
        );                    
        
    }
        


    /** 
     * Adds a new template rule to this stylesheet
     */    
    public void addTemplate( Rule rule ) {
        stylesheet.addRule( rule );
    }
    
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {
        // for use by inner classes
        this.output = output;
        
        try {        
            // run the body to add the rules
            getBody().run(context, output);
            
            stylesheet.setModeName( getMode() );
            stylesheet.run( source );
        }
        finally {
            
            // help the GC
            this.output = null;
            stylesheet.clear();        
        }
    }
    
    
    // Properties
    //-------------------------------------------------------------------------                
    
    /** 
     * Getter for property mode.
     * @return Value of property mode.
     */
    public String getMode() {
        return mode;
    }
    
    /** 
     * Setter for property mode.
     * @param mode New value of property mode.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }   

    /** Sets the source on which the stylesheet will run
     */
    public void setSource(Object source) {
        this.source = source;
    }    
    
    public Stylesheet getStylesheet() {
        return stylesheet;
    }
}
