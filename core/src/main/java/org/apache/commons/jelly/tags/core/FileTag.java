/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 */
public class FileTag extends TagSupport {
    private boolean doAppend = false;
    private String var;
    private String name;
    private boolean omitXmlDeclaration = false;
    private String outputMode = "xml";
    private boolean prettyPrint;
    private String encoding;

    public FileTag(){
    }

    /**
     * A Factory method to create a new XMLOutput from the given Writer.
     */
    protected XMLOutput createXMLOutput(final Writer writer) {

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

        final boolean isHtml = outputMode != null && outputMode.equalsIgnoreCase( "html" );
        final XMLWriter xmlWriter = isHtml
            ? new HTMLWriter(writer, format)
            : new XMLWriter(writer, format);

        xmlWriter.setEscapeText(isEscapeText());

        final XMLOutput answer = new XMLOutput() {
            @Override
            public void close() throws IOException {
                xmlWriter.close();
            }
        };
        answer.setContentHandler(xmlWriter);
        answer.setLexicalHandler(xmlWriter);
        return answer;
    }

    // Properties
    //-------------------------------------------------------------------------

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        try {
            if ( name != null ) {
                final String encoding = this.encoding != null ? this.encoding : "UTF-8";
                final Writer writer = new OutputStreamWriter( new FileOutputStream( name, doAppend ), encoding );
                writeBody(writer);
            }
            else if (var != null) {
                final StringWriter writer = new StringWriter();
                writeBody(writer);
                final String result = writer.toString();
                final Object varValue = context.getVariable(var);
                // if we're appending, and var is an instance of string, append it.
                if (doAppend && varValue instanceof String) {
                    context.setVariable(var, varValue + result);
                } else {
                    context.setVariable(var, result);
                }
            }
            else {
                throw new JellyTagException( "This tag must have either the 'name' or the 'var' variables defined" );
            }
        } catch (final FileNotFoundException | UnsupportedEncodingException e) {
            throw new JellyTagException(e);
        } catch (final SAXException e) {
            throw new JellyTagException("could not write file", e);
        }
    }

    /**
     * Returns the var.
     * @return String
     */
    public String getVar() {
        return var;
    }

    /**
     * Sets wether to append at the end of the file
     * (not really something you normally do with an XML file).
     */
    public void setAppend(final boolean doAppend) {
        this.doAppend = doAppend;
    }

    /**
     * Sets the XML encoding mode, which defaults to UTF-8
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Sets the file name for the output
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets whether the XML declaration should be output or not
     */
    public void setOmitXmlDeclaration(final boolean omitXmlDeclaration) {
        this.omitXmlDeclaration = omitXmlDeclaration;
    }


    /**
     * Sets the output mode, whether XML or HTML
     */
    public void setOutputMode(final String outputMode) {
        this.outputMode = outputMode;
    }

    /**
     * Sets whether pretty printing mode is turned on. The default is off so that whitespace is preserved
     */
    public void setPrettyPrint(final boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    /**
     * Sets the var.
     * @param var The var to set
     */
    public void setVar(final String var) {
        this.var = var;
    }

    /**
     * Writes the body fo this tag to the given Writer
     */
    protected void writeBody(final Writer writer) throws SAXException, JellyTagException {

        final XMLOutput newOutput = createXMLOutput(writer);
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
            try { newOutput.close(); } catch (final IOException e) {}
        }
    }
}
