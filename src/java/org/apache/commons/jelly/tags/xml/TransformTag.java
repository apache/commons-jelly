/*
 * $Header:$
 * $Revision:$
 * $Date:$
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
 * $Id:$
 */
package org.apache.commons.jelly.tags.xml;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

/** A tag which parses some XML, applies an xslt transform to it
  * and defines a variable with the transformed Document.
  * The XML can either be specified as its body or can be passed in via the
  * xml property which can be a Reader, InputStream, URL or String URI.
  *
  * The XSL can be passed in via the
  * xsl property which can be a Reader, InputStream, URL or String URI.
  *
  * @author <a href="mailto:robert@leftwoch.info">Robert Leftwich</a>
  * @version $Revision: 1.00 $
  */
public class TransformTag extends ParseTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TransformTag.class);

    /** The xsl to parse, either a String URI, a Reader or InputStream */
    private Object xsl;

    /** The xsl transformer factory */
    private TransformerFactory tf;

    /**
     * Constructor for TransformTag.
     */
    public TransformTag() {
        super();
        tf = TransformerFactory.newInstance();
    }

    // Tag interface
    //-------------------------------------------------------------------------

    /**
     * Process this tag instance
     *
     * @param output The pipeline for xml events
     * @throws Exception - when required attributes are missing
     */
    public void doTag(XMLOutput output) throws Exception {
        if (this.getVar() == null) {
            throw new IllegalArgumentException("The var attribute cannot be null");
        }
        Document xmlDocument = this.getXmlDocument(output);
        Document xslDocument = this.parse(this.xsl);

        tf.setURIResolver(createURIResolver());
        Transformer transformer = tf.newTransformer(new DocumentSource(xslDocument));

        DocumentSource xmlDocSource = new DocumentSource(xmlDocument);
        DocumentResult result = new DocumentResult();
        
        transformer.transform(xmlDocSource, result);

        Document transformedDoc = result.getDocument();

        context.setVariable(getVar(), transformedDoc);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Sets the source of the XSL which is either a String URI, Reader or InputStream
     *
     * @param xsl    The source of the xsl
     */
    public void setXsl(Object xsl) {
        this.xsl = xsl;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Parses the given source
     * @see org.apache.commons.jelly.tags.xml.ParseTagSupport#parse(Object)
     */
    protected Document parse(Object source) throws Exception {
        if (source instanceof Document) {
          return (Document) source;
        } else {
          return super.parse(source);
        }
    }

    /**
     * Creates a new URI Resolver so that URIs inside the XSLT document can be resolved using
     * the JellyContext
     */
    protected URIResolver createURIResolver() {
        return new URIResolver() {
            public Source resolve(String href, String base)
                throws TransformerException {

                if (log.isDebugEnabled() ) {
                    log.info( "base: " + base + " href: " + href );
                }
                                        
                // pass if we don't have a systemId
                if (href == null)
                    return null;

                // @todo 
                // #### this is a pretty simplistic implementation.
                // #### we should really handle this better such that if
                // #### base is specified as an absolute URL 
                // #### we trim the end off it and append href
                return new StreamSource(context.getResourceAsStream(href));
            }
        };
    }
}
