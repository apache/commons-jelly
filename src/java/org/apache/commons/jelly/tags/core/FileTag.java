/*
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
 */
package org.apache.commons.jelly.tags.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/** 
 * A tag that pipes its body to a file.
 *
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 */
public class FileTag extends TagSupport 
{
    private String name;
    private boolean omitXmlDeclaration = false;
    private String outputMode = "xml";
    private boolean prettyPrint;
    private String encoding;
    
    public FileTag(){
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(final XMLOutput output) throws Exception {
        if ( name == null ) {
            throw new MissingAttributeException( "name" );
        }
        XMLOutput newOutput = createXMLOutput();
        try {
            newOutput.startDocument();
            invokeBody(newOutput);
            newOutput.endDocument();
        }
        finally {
            newOutput.close();
        }
    }
    
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
        
    // Implementation methods
    //------------------------------------------------------------------------- 
    protected XMLOutput createXMLOutput() throws Exception {
        
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
                    
        OutputStream out = new FileOutputStream(name);
        
        boolean isHtml = outputMode != null && outputMode.equalsIgnoreCase( "html" );
        final XMLWriter xmlWriter = (isHtml) 
            ? new HTMLWriter(out, format)
            : new XMLWriter(out, format);

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
