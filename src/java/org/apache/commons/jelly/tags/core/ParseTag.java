/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/ParseTag.java,v 1.1 2003/03/06 11:50:41 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/06 11:50:41 $
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
 * $Id: ParseTag.java,v 1.1 2003/03/06 11:50:41 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.core;

import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/** 
 * Parses the output of this tags body or of a given String as a Jelly script
 * then either outputting the Script as a variable or executing the script.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class ParseTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ParseTag.class);

    /** The variable that will be generated for the document */
    private String var;
    
    /** The markup text to be parsed */
    private String text;

    /** The XMLReader used to parser the document */
    private XMLReader xmlReader;

	/** The Jelly parser */
	private XMLParser jellyParser;
	
    public ParseTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------                

    /* (non-Javadoc)
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    public void doTag(XMLOutput output)
        throws MissingAttributeException, JellyTagException {

        String text = getText();
        if (text != null) {
            parseText(text);
        }
        else {            
            parseBody(output);
        }
	
        Script script = getJellyParser().getScript();
        if (var != null) {
            context.setVariable(var, script);
        }
        else {
            // invoke the script
            script.run(context, output);
        }
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

    /**
     * Returns the text to be parsed
     * @return String
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text to be parsed by this parser
     * @param text The text to be parsed by this parser
     */
    public void setText(String text) {
        this.text = text;
    }

    
    /** @return the XMLReader used for parsing, creating one lazily if need be  */
    public XMLReader getXMLReader() throws ParserConfigurationException, SAXException {
        if (xmlReader == null) {
            xmlReader = createXMLReader();
        }
        return xmlReader;
    }

    /** Sets the XMLReader used for parsing */
    public void setXMLReader(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }


    /**
     * @return XMLParser
     */
    public XMLParser getJellyParser() {
        if (jellyParser == null) {
            jellyParser = createJellyParser();
        }
	    return jellyParser;
    }

    /**
     * Sets the jellyParser.
     * @param jellyParser The jellyParser to set
     */
    public void setJellyParser(XMLParser jellyParser) {
        this.jellyParser = jellyParser;
    }
    

    // Implementation methods
    //-------------------------------------------------------------------------                

    /**
     * Factory method to create a new XMLReader
     */    
    protected XMLReader createXMLReader() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        return parser.getXMLReader(); 
    }
    
    
    /**
     * Parses the body of this tag and returns the parsed document
     */
    protected void parseBody(XMLOutput output) throws JellyTagException {
        ContentHandler handler = getJellyParser();
        XMLOutput newOutput = new XMLOutput();
        
        try {
            handler.startDocument();
            invokeBody(newOutput);
            handler.endDocument();
        } 
        catch (SAXException e) {
            throw new JellyTagException(e);
        }
    }
    
    /**
     * Parses the give piece of text as being markup
     */
    protected void parseText(String text) throws JellyTagException {
        if ( log.isDebugEnabled() ) {
            log.debug( "About to parse: " + text );
        }
        
        try {
            getXMLReader().parse( new InputSource( new StringReader( text ) ) );
        } 
        catch (Exception e) {
            throw new JellyTagException(e);
        }
    }
    
    /**
     * Factory method to create a new Jelly parser
     * @return XMLParser
     */
    protected XMLParser createJellyParser() {
        XMLParser answer = new XMLParser();
        answer.setContext(context); 
        return answer;
    }
}
