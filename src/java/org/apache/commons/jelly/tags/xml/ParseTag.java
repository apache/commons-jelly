/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/xml/Attic/ParseTag.java,v 1.1 2002/02/11 00:27:41 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2002/02/11 00:27:41 $
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
 * $Id: ParseTag.java,v 1.1 2002/02/11 00:27:41 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.jelly.Context;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import org.xml.sax.SAXException;

/** A tag which parses some XML and defines a variable with the parsed Document.
  * The XML can either be specified as its body or can be passed in via the
  * source property which can be a Reader, InputStream, URL or String URI.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class ParseTag implements Tag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogSource.getInstance( ParseTag.class );

    /** The variable that will be generated for the document */
    private String var;
    /** The source to parse, either a String URI, a Reader or InputStream */
    private Object source;
    
    // Optional properties not defined in JSTL
    
    /** whether XML validation is enabled or disabled */
    private boolean validate;
    /** The SAXReader used to parser the document */
    private SAXReader saxReader;
    
    public ParseTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void run(Context context, Writer writer, Script body) throws Exception {
        if ( var == null ) {
            throw new IllegalArgumentException( "The var attribute cannot be null" );
        }
        Document document = null;
        if ( source == null ) {
            // parse body
            StringWriter buffer = new StringWriter();
            body.run( context, buffer );
            
            if ( log.isDebugEnabled() ) {
                log.debug( "Evaluated body: " + body );
                log.debug( "About to parse: " + buffer.toString() );
            }
            document = getSAXReader().read( new StringReader( buffer.toString() ) );
        }
        else {
            if ( source instanceof String ) {
                document = getSAXReader().read( (String) source );
            }
            else if ( source instanceof Reader ) {
                document = getSAXReader().read( (Reader) source );
            }
            else if ( source instanceof InputStream ) {
                document = getSAXReader().read( (InputStream) source );
            }
            else if ( source instanceof URL ) {
                document = getSAXReader().read( (URL) source );
            }
            else {
                throw new IllegalArgumentException( 
                    "Invalid source argument. Must be a String, Reader, InputStream or URL."
                    + " Was type; " + source.getClass().getName() 
                    + " with value: " + source 
                );
            }
        }
        context.setVariable( var, document );
    }

    // Properties
    //-------------------------------------------------------------------------                
    
    /** The variable name that will be used for the Document variable created
     */
    public String getVar() {
        return var;
    }
    
    /** Sets the variable name that will be used for the Document variable created
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /** @return whether XML validation is enabled or disabled */
    public boolean getValidate() {
        return validate;
    }
    
    /** Sets whether XML validation is enabled or disabled */
    public void setValidate(boolean validate) {
        this.validate = validate;
    }
    
    /** @return the SAXReader used for parsing, creating one lazily if need be  */
    public SAXReader getSAXReader() throws SAXException {
        if ( saxReader == null ) {
            saxReader = new SAXReader(validate);
        }
        return saxReader;
    }
    
    /** Sets the SAXReader used for parsing */
    public void setSAXReader(SAXReader saxReader) {
        this.saxReader = saxReader;
    }    
}
