/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/taglibs/beanshell/src/java/org/apache/commons/jelly/tags/beanshell/BeanShellExpressionFactory.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2002/05/21 07:58:55 $
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
 * $Id: BeanShellExpressionFactory.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.validate;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFilter;
import org.iso_relax.verifier.VerifierHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

/** 
 * This tag validates its body using a schema Verifier which can
 * validate against DTDs, XML Schema, RelaxNG, Relax or TREX.
 * Any JARV compliant Verifier could be used.
 * The error messages are output as XML events so that they can be styled by the parent tag.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class ValidateTag extends TagSupport {

    /** The verifier that this tag will use */
    private Verifier verifier;

    /** The SAX ErrorHandler */
    private ErrorHandler errorHandler;
    
    /** The boolean flag for whether the XML is valid */
    private String var;
       
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(final XMLOutput output) throws Exception {    
        if ( verifier == null ) {
            throw new MissingAttributeException("verifier");
        }
        boolean valid = false;
        
        // evaluate the body using the current Verifier 
        if ( errorHandler != null ) {
            // we are redirecting errors to another handler
            // so just filter the body
            VerifierFilter filter = verifier.getVerifierFilter();     
            
            // now install the current output in the filter chain...
            // ####
            
            ContentHandler handler = filter.getContentHandler();
            handler.startDocument();
            invokeBody( new XMLOutput( handler ) );
            handler.endDocument();
            valid = filter.isValid();            
        }
        else {
	        // outputting the errors to the current output
	        verifier.setErrorHandler(
	            new ErrorHandler() {
	                public void error(SAXParseException exception) throws SAXException {
	                    outputException(output, "error", exception);
	                }
	                
	                public void fatalError(SAXParseException exception) throws SAXException {
	                    outputException(output, "fatalError", exception);
	                }
	                
	                public void warning(SAXParseException exception) throws SAXException {
	                    outputException(output, "warning", exception);
	                }
	            }
	        );
	
	        VerifierHandler handler = verifier.getVerifierHandler();     
            handler.startDocument();
	        invokeBody( new XMLOutput( handler ) );
            handler.endDocument();
            valid = handler.isValid();            
        }
        if (var != null ) {
            Boolean value = (valid) ? Boolean.TRUE : Boolean.FALSE;
            context.setVariable(var, value);
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                    

    /** 
     * Sets the schema Verifier that this tag will use to verify its body
     * 
     * @jelly:required
     */
    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
    }
    
    /** 
     * Sets the SAX ErrorHandler which is used to capture 
     * XML validation events. 
     * If an ErrorHandler is specified
     * then this tag will output its body and redirect all error messages
     * to the ErrorHandler.
     * If no ErrorHandler is specified then this tag will just output the
     * error messages as XML events
     * 
     * @jelly:optional
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    /** 
     * Sets the name of the variable that will contain a boolean flag for whether or 
     * not the XML is valid. 
     * 
     * @jelly:optional
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Outputs the given validation exception as XML to the output
     */
    protected void outputException(XMLOutput output, String name, SAXParseException e) throws SAXException {
        AttributesImpl attributes = new AttributesImpl();
        String uri = "";
        String type = "CDATA";
        attributes.addAttribute( uri, "column", "column", type, Integer.toString( e.getColumnNumber() ) );
        attributes.addAttribute( uri, "line", "line", type, Integer.toString( e.getLineNumber() ) );

        String publicID = e.getPublicId();
        if ( publicID != null && publicID.length() > 0 ) {
            attributes.addAttribute( uri, "publicID", "publicID", type, publicID );
        }
        String systemID = e.getSystemId();
        if ( systemID != null && systemID.length() > 0 ) {
            attributes.addAttribute( uri, "systemID", "systemID", type, systemID );
        }

        output.startElement( uri, name, name, attributes );
        output.write( e.getMessage() );
        output.endElement( uri, name, name );
    }
    

}
