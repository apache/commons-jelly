/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/xml/Attic/XPathExpression.java,v 1.9 2002/11/13 09:03:31 jstrachan Exp $
 * $Revision: 1.9 $
 * $Date: 2002/11/13 09:03:31 $
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
 * $Id: XPathExpression.java,v 1.9 2002/11/13 09:03:31 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.expression.ExpressionSupport;
import org.apache.commons.jelly.impl.TagScript;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jaxen.XPath;
import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.VariableContext;

/** An expression which returns an XPath object.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.9 $
  */
public class XPathExpression extends ExpressionSupport implements VariableContext {
    
    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(XPathExpression.class);

    private XPath xpath;
    private JellyContext context;
    
    public XPathExpression() {
    }

    public XPathExpression(XPath xpath, TagScript tagScript) {
        this.xpath = xpath;
        
        Map namespaceContext = tagScript.getNamespaceContext();
        
        Map uris = createUriMap(namespaceContext);
        
        if (log.isDebugEnabled()) {
        	log.debug( "Setting the namespace context to be: " + uris );
        }
        
        xpath.setNamespaceContext( new SimpleNamespaceContext( uris ) );
    }
    
    public String toString() {
        return xpath.toString();
    }
            
    // Expression interface
    //------------------------------------------------------------------------- 
    public Object evaluate(JellyContext context) {
        this.context = context;
        xpath.setVariableContext(this);
        return xpath;
    }
    
    // VariableContext interface
    //------------------------------------------------------------------------- 
    public Object getVariableValue(
        String namespaceURI,
        String prefix,
        String localName) {
            
        Object value = context.getVariable(localName);
        
        //log.debug( "Looking up XPath variable of name: " + localName + " value is: " + value );            
        
        return value;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------
    
    /**
     * Factory method to create a synchronized Map of non-null and non-blank
     * namespace prefixes to namespace URIs     */ 
    protected Map createUriMap(Map namespaceContext) {
        // now lets clone the Map but ignoring default or null prefixes
        Map uris = new Hashtable(namespaceContext.size());
        for (Iterator iter = namespaceContext.entrySet().iterator(); iter.hasNext(); ) {
        	Map.Entry entry = (Map.Entry) iter.next();
        	String prefix = (String) entry.getKey();
        	if (prefix != null && prefix.length() != 0) {
        		uris.put(prefix, entry.getValue());
        	}
        }
        return uris;
    }
}
