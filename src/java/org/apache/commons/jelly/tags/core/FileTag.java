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
 */
package org.apache.commons.jelly.tags.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.SafeContentHandler;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

/** 
 * A tag that pipes its body to a file denoted by the name attribute or to an in memory String
 * which is then output to a variable denoted by the var variable.
 *
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 */
public class FileTag extends TagSupport {
    private String var;
    private String name;
    private boolean omitXmlDeclaration = false;
    private String outputMode = "xml";
    private boolean prettyPrint;
    private String encoding;
    
    // should text be escaped - default to true for compatability
    private boolean escapeText = true;

    public FileTag(){
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(final XMLOutput output) throws JellyTagException {
        try {
            if ( name != null ) {
                String encoding = (this.encoding != null) ? this.encoding : "UTF-8";
                Writer writer = new OutputStreamWriter( new FileOutputStream( name ), encoding );
                writeBody(writer);
            }
            else if (var != null) {
                StringWriter writer = new StringWriter();
                writeBody(writer);
                context.setVariable(var, writer.toString());
            }
            else {
                throw new JellyTagException( "This tag must have either the 'name' or the 'var' variables defined" );
            }
        } catch (FileNotFoundException e) {
            throw new JellyTagException(e);
        } catch (UnsupportedEncodingException e) {
            throw new JellyTagException(e);
        } catch (SAXException e) {
            throw new JellyTagException("could not write file",e);
        }
    }
        
    // Properties
    //------------------------------------------------------------------------- 
    
    /**
     * Sets the file name for the output
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets whether the XML declaration should be output or not 
     */
    public void setOmitXmlDeclaration(boolean omitXmlDeclaration) {
        this.omitXmlDeclaration = omitXmlDeclaration;
    }
    
    
    /**
     * Sets the output mode, whether XML or HTML
     */
    public void setOutputMode(String outputMode) {
        this.outputMode = outputMode;
    }

    /**
     * Sets whether pretty printing mode is turned on. The default is off so that whitespace is preserved
     */
    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
        
    /**
     * Sets the XML encoding mode, which defaults to UTF-8
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
        
    /**
     * Returns the var.
     * @return String
     */
    public String getVar() {
        return var;
    }

    /**
     * Sets the var.
     * @param var The var to set
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Returns whether the body of this tag will be escaped or not.
     */
    public boolean isEscapeText() {
        return escapeText;
    }

    /**
     * Sets whether the body of the tag should be escaped as text (so that &lt; and &gt; are
     * escaped as &amp;lt; and &amp;gt;), which is the default or leave the text as XML.
     */
    public void setEscapeText(boolean escapeText) {
        this.escapeText = escapeText;
    }

    // Implementation methods
    //------------------------------------------------------------------------- 

    /**
     * Writes the body fo this tag to the given Writer
     */
    protected void writeBody(Writer writer) throws SAXException, JellyTagException {

        XMLOutput newOutput = createXMLOutput(writer);
        try {
            // we need to avoid multiple start/end document events
            newOutput.setContentHandler(
            	new SafeContentHandler(newOutput.getContentHandler())
            );
            newOutput.startDocument();
            invokeBody(newOutput);
            newOutput.endDocument();
        }  
        finally {
            try { newOutput.close(); } catch (IOException e) {}
        }
    }
    
    /**
     * A Factory method to create a new XMLOutput from the given Writer.
     */
    protected XMLOutput createXMLOutput(Writer writer) {
        
        OutputFormat format = null;
        if (prettyPrint) {
            format = OutputFormat.createPrettyPrint();
        }
        else {
            format = new OutputFormat();
        }
        if ( encoding != null ) {
            format.setEncoding( encoding );
        }           
        if ( omitXmlDeclaration ) {
            format.setSuppressDeclaration(true);
        }
                    
        boolean isHtml = outputMode != null && outputMode.equalsIgnoreCase( "html" );
        final XMLWriter xmlWriter = (isHtml) 
            ? new HTMLWriter(writer, format)
            : new XMLWriter(writer, format);

        xmlWriter.setEscapeText(isEscapeText());

        XMLOutput answer = new XMLOutput() {
            public void close() throws IOException {
                xmlWriter.close();
            }
        };
        answer.setContentHandler(xmlWriter);
        answer.setLexicalHandler(xmlWriter);
        return answer;
    }
}
