/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/xml/Attic/ParseTag.java,v 1.8 2002/05/17 15:18:13 jstrachan Exp $
 * $Revision: 1.8 $
 * $Date: 2002/05/17 15:18:13 $
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
 * $Id: ParseTag.java,v 1.8 2002/05/17 15:18:13 jstrachan Exp $
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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;

import org.xml.sax.SAXException;

/** A tag which parses some XML and defines a variable with the parsed Document.
  * The XML can either be specified as its body or can be passed in via the
  * xml property which can be a Reader, InputStream, URL or String URI.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.8 $
  */
public class ParseTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ParseTag.class);

    /** The variable that will be generated for the document */
    private String var;

    /** The xml to parse, either a String URI, a Reader or InputStream */
    private Object xml;

    // Optional properties not defined in JSTL
    /** whether XML validation is enabled or disabled */
    private boolean validate;

    /** The SAXReader used to parser the document */
    private SAXReader saxReader;

    public ParseTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        if (var == null) {
            throw new IllegalArgumentException("The var attribute cannot be null");
        }
        Document document = null;
        if (xml == null) {
            SAXContentHandler handler = new SAXContentHandler();
            XMLOutput newOutput = new XMLOutput(handler);
            handler.startDocument();
            getBody().run(context, newOutput);
            handler.endDocument();
            document = handler.getDocument();
            /*
                        // the following is inefficient as it requires a parse of the text
                        // but is left here in the code to see how it could be done.
            
                        String text = getBodyText();
                        
                        if ( log.isDebugEnabled() ) {
                            log.debug( "About to parse: " + text );
                        }
                        document = getSAXReader().read( new StringReader( text ) );
            */
        }
        else {
            if (xml instanceof String) {
                document = getSAXReader().read((String) xml);
            }
            else if (xml instanceof Reader) {
                document = getSAXReader().read((Reader) xml);
            }
            else if (xml instanceof InputStream) {
                document = getSAXReader().read((InputStream) xml);
            }
            else if (xml instanceof URL) {
                document = getSAXReader().read((URL) xml);
            }
            else {
                throw new IllegalArgumentException(
                    "Invalid xml argument. Must be a String, Reader, InputStream or URL."
                        + " Was type; "
                        + xml.getClass().getName()
                        + " with value: "
                        + xml);
            }
        }
        context.setVariable(var, document);
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
    
    /** Sets the source of the XML which is either a String URI, Reader or InputStream */
    public void setXml(Object xml) {
        this.xml = xml;
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
        if (saxReader == null) {
            saxReader = new SAXReader(validate);
        }
        return saxReader;
    }

    /** Sets the SAXReader used for parsing */
    public void setSAXReader(SAXReader saxReader) {
        this.saxReader = saxReader;
    }
}
