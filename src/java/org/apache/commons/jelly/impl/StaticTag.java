/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/impl/StaticTag.java,v 1.13 2002/12/11 12:40:55 jstrachan Exp $
 * $Revision: 1.13 $
 * $Date: 2002/12/11 12:40:55 $
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
 * $Id: StaticTag.java,v 1.13 2002/12/11 12:40:55 jstrachan Exp $
 */
package org.apache.commons.jelly.impl;

import org.apache.commons.jelly.DynaTagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.helpers.AttributesImpl;

/** 
 * <p><code>StaticTag</code> represents a static XML element
 * which echos itself to XMLOutput when it is invoked.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.13 $
 */

public class StaticTag extends DynaTagSupport {
    
    /** The namespace URI */
    private String uri;
    
    /** The qualified name */
    private String qname;
    
    /** The local name */
    private String localName;
    
    /** The XML Attributes */
    private AttributesImpl attributes = new AttributesImpl();
    
    public StaticTag() {
    }
    
    public StaticTag(String uri, String localName, String qname) {
        this.uri = uri;
        this.localName = localName;
        this.qname = qname;
    }
    
    public String toString() {
        return super.toString() + "[qname=" + qname + ";attributes=" + attributes + "]";
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {
        output.startElement(uri, localName, qname, attributes);
        invokeBody(output);
        output.endElement(uri, localName, qname);
    }
    
    // DynaTag interface
    //-------------------------------------------------------------------------                    
    public void setAttribute(String name, Object value) {
        // ### we'll assume that all attributes are in no namespace!
        // ### this is severely limiting!
        // ### - Tag attributes should allow for namespace aware 
        int index = attributes.getIndex("", name);
        if (index >= 0) {
            attributes.removeAttribute(index);
        }
        // treat null values as no attribute 
        if (value != null) {
            attributes.addAttribute("", name, name, "CDATA", value.toString());
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                    
    public String getUri() {
        return uri;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getQName() {
        return qname;
    }
    
    public void setQName(String qname) {
        this.qname = qname;
        int idx = qname.indexOf(':');
        if (idx >= 0) {
            this.localName = qname.substring(idx + 1);
        }
        else {
            this.localName = qname;
        }
    }
    
    public String getLocalName() {
        return localName;
    }
    
    public void setLocalName(String localName) {
        this.localName = localName;
        if (qname == null || !qname.endsWith(localName)) {
            localName = qname;
        }
    }
}
