/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/parser/DefaultNamespaceFilter.java,v 1.2 2002/10/14 19:46:22 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2002/10/14 19:46:22 $
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
 * $Id: DefaultNamespaceFilter.java,v 1.2 2002/10/14 19:46:22 morgand Exp $
 */
package org.apache.commons.jelly.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * XMLFilter that can provide a default namespace when
 * one has not been declared by the XML document.  Note:
 * this class does not address the namespace of attributes.
 * 
 * @author Morgan Delagrange
 */
public class DefaultNamespaceFilter extends XMLFilterImpl {

    protected String uriDefault = null;

    /**
     * Filter for undefined an undefined namespace
     * 
     * @param defaultNamespace
     *               uri for the jelly namespace
     * @param reader XMLReader to filter
     */
    public DefaultNamespaceFilter(String defaultNamespace, XMLReader reader) {
        super(reader);
        this.uriDefault = defaultNamespace;
    }

    /**
     * All incoming empty URIs will be remapped to the default.
     * 
     * @param prefix incoming prefix
     * @param uri    URI to check and potentially replace
     * @exception SAXException
     */
    public void startPrefixMapping(java.lang.String prefix,
                                   java.lang.String uri)
    throws SAXException {

        if (uri.equals("")) {
            super.startPrefixMapping(prefix,this.uriDefault);
        } else {
            super.startPrefixMapping(prefix,uri);
        }
    }

    /**
     * All incoming empty URIs will be remapped to the default.
     * 
     * @param uri       URI to check and potentially replace
     * @param localName
     * @param qName
     * @param atts
     * @exception SAXException
     */
    public void startElement(java.lang.String uri,
                             java.lang.String localName,
                             java.lang.String qName,
                             Attributes atts)
    throws SAXException {

        if (uri.equals("")) {
            super.startElement(this.uriDefault,localName,qName,atts);
        } else {
            super.startElement(uri,localName,qName,atts);
        }

    }

    /**
     * All incoming empty URIs will be remapped to the default.
     * 
     * @param namespaceURI
     *                  URI to check and potentially replace
     * @param localName
     * @param qName
     * @exception SAXException
     */
    public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException {
        if (namespaceURI.equals("")) {
            super.endElement(this.uriDefault,localName,qName);
        } else {
            super.endElement(namespaceURI,localName,qName);
        }
    }
}