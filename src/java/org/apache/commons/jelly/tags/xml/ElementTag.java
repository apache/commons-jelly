/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/xml/src/java/org/apache/commons/jelly/tags/xml/ElementTag.java,v 1.1 2003/01/15 23:56:45 dion Exp $
 * $Revision: 1.1 $
 * $Date: 2003/01/15 23:56:45 $
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
 * $Id: ElementTag.java,v 1.1 2003/01/15 23:56:45 dion Exp $
 */
package org.apache.commons.jelly.tags.xml;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/** A tag to produce an XML element which can contain other attributes 
  * or elements like the <code>&lt;xsl:element&gt;</code> tag.
  *
  * @author James Strachan
  * @version $Revision: 1.1 $
  */
public class ElementTag extends TagSupport {
    
    /** The namespace URI */
    private String namespace;
    
    /** The qualified name */
    private String name;
    
    /** The XML Attributes */
    private AttributesImpl attributes = new AttributesImpl();

	/** flag set if attributes are output */
	private boolean outputAttributes;
    
    public ElementTag() {
    }

	/**
	 * Sets the attribute of the given name to the specified value.
	 * 	 * @param name of the attribute	 * @param value of the attribute	 * @throws JellyException if the start element has already been output. 
	 *   Attributes must be set on the outer element before any content 
	 *   (child elements or text) is output	 */
    public void setAttributeValue( String name, String value ) throws JellyException {
    	if (outputAttributes) {
    		throw new JellyException(
				"Cannot set the value of attribute: " 
    			+ name + " as we have already output the startElement() SAX event"
			);
    	}
    	
        // ### we'll assume that all attributes are in no namespace!
        // ### this is severely limiting!
        // ### we should be namespace aware
        int index = attributes.getIndex("", name);
        if (index >= 0) {
            attributes.removeAttribute(index);
        }
        // treat null values as no attribute 
        if (value != null) {
            attributes.addAttribute("", name, name, "CDATA", value);
        }
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {
        int idx = name.indexOf(':');
        final String localName = (idx >= 0) 
        	? name.substring(idx + 1)
        	: name;
        
        outputAttributes = false;
        
        XMLOutput newOutput = new XMLOutput(output) {

			// add an initialize hook to the core content-generating methods
			        	
		    public void startElement(
		        String uri,
		        String localName,
		        String qName,
		        Attributes atts)
		        throws SAXException {
				initialize();        	
		        super.startElement(uri, localName, qName, atts);
		    }
		
		    public void endElement(String uri, String localName, String qName)
		        throws SAXException {
				initialize();        	
		        super.endElement(uri, localName, qName);
		    }
		
		    public void characters(char ch[], int start, int length) throws SAXException {
				initialize();        	
		        super.characters(ch, start, length);
		    }
		
		    public void ignorableWhitespace(char ch[], int start, int length)
		        throws SAXException {
				initialize();        	
		        super.ignorableWhitespace(ch, start, length);
		    }
		    public void processingInstruction(String target, String data)
		        throws SAXException {
				initialize();        	
		        super.processingInstruction(target, data);
		    }
		
        	/** 
        	 * Ensure that the outer start element is generated 
        	 * before any content is output        	 */ 
        	protected void initialize() throws SAXException {
        		if (!outputAttributes) {
			        super.startElement(namespace, localName, name, attributes);
			        outputAttributes = true;
        		}
        	}
        };
        
        invokeBody(newOutput);
        
        if (!outputAttributes) {
	        output.startElement(namespace, localName, name, attributes);
	        outputAttributes = true;
        }
        
        output.endElement(namespace, localName, name);
        attributes.clear();
    }
    
    // Properties
    //-------------------------------------------------------------------------
    
    /** 
     * @return the qualified name of the element
     */
    public String getName() {
        return name;
    }
    
    /** 
     * Sets the qualified name of the element
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the namespace URI of the element
     */
    public String getURI() {
        return namespace;
    }
    
    /**
     * Sets the namespace URI of the element
     */
    public void setURI(String namespace) {
        this.namespace = namespace;
    }
}
