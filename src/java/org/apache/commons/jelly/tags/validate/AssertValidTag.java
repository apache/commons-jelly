/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/taglibs/beanshell/src/java/org/apache/commons/jelly/tags/beanshell/BeanShellExpressionFactory.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2002/05/21 07:58:55 $
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
 * $Id: BeanShellExpressionFactory.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.validate;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.junit.JellyAssertionFailedError;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

/** 
 * This tag performs an assertion that the tags body contains XML
 * which matches a givem schema validation. This tag is used with 
 * JellyUnit to implement an assertion.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class AssertValidTag extends ValidateTag {

    private StringBuffer buffer = new StringBuffer();


/*        
    public AssertValidTag() {
        setErrorHandler(
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
    }
*/    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(final XMLOutput output) throws Exception {    
        buffer.setLength(0);
        super.doTag(output);
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Processes whether or not the document is valid.
     * Derived classes can overload this method to do different things, such 
     * as to throw assertion exceptions etc.
     */    
    protected void handleValid(boolean valid) throws Exception {
        super.handleValid(valid);
        
        if ( ! valid ) {
            String message = buffer.toString();
            throw new JellyAssertionFailedError( "The XML is not valid according to the schema: " + message );
        }
    }
    
    /**
     * Outputs the given validation exception as XML to the output
     */
    protected void outputException(XMLOutput output, String name, SAXParseException e) throws SAXException {
        buffer.append( name );
        buffer.append( " : line: " );
        buffer.append( e.getLineNumber() );
        buffer.append( " column: " );
        buffer.append( e.getColumnNumber() );
        buffer.append( " message: " );
        buffer.append( e.getMessage() );
        buffer.append( '\n' );
    }
    
}
