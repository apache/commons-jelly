/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.betwixt;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

/** 
 * Parses some XML specified via the given URI (which can be relative or an absolute URL) and outputs the
 * parsed object. Typically this tag is customized by setting the introspector attribute or nesting a child
 * introspector tag inside it.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class ParseTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ParseTag.class);

    /** the BeanReader used to parse the XML */
    private BeanReader reader = new BeanReader();

    private String uri;
    private String var;
    private String rootClass;
    private String path;
    private XMLIntrospector introspector;
    private boolean useContextClassLoader;
    private ClassLoader classLoader;
    
    
    public ParseTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if ( var == null ) {
            throw new MissingAttributeException( "var" );
        }
        if ( rootClass == null ) {
            throw new MissingAttributeException( "rootClass" );
        }
        
        reader.setXMLIntrospector(getIntrospector());
        
        Class theClass = null;
        try {
            theClass = getClassLoader().loadClass( rootClass );
        }
        catch (Exception e) {
            throw new JellyTagException( "Could not load class called: " + rootClass, e );
        }
        
        if ( theClass == null ) {
            throw new JellyTagException( "Could not load class called: " + rootClass );
        }

        try {       
            if ( path != null ) {
                reader.registerBeanClass( path, theClass );
            }
            else {
                reader.registerBeanClass( theClass );
            }
        }
        catch (IntrospectionException e) {
            throw new JellyTagException(e);
        }
        
        Object value = null;
        if ( uri != null ) {
            invokeBody(output);
        
            try {
                URL url = context.getResource( uri );
                value = reader.parse( url.toString() );
            } catch (IOException e) {
                throw new JellyTagException(e);
            } catch (SAXException e) {
                throw new JellyTagException(e);
            }
        }
        else {

            // invoke the body and pass that into the reader
            XMLOutput newOutput = new XMLOutput( reader );
            
            invokeBody(newOutput);
            
            value = reader.getRoot();
        }
        context.setVariable( var, value );
    }
    
    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * @return the introspector to be used, lazily creating one if required.
     */
    public XMLIntrospector getIntrospector() {
        if (introspector == null) {
            introspector = new XMLIntrospector();
        }
        return introspector;
    }
    /**
     * Sets the Betwixt XMLIntrospector instance used to define the metadata for how a 
     * bean should appear as XML.
     */
    public void setIntrospector(XMLIntrospector introspector) {
        this.introspector = introspector;
    }
    
    /**
     * Sets the URI from which XML is parsed. This can be relative to this Jelly script, use
     * an absolute URI or a full URL
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    /**
     * Sets the variable name to output with the result of the XML parse.
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /**
     * Sets the name of the root class to use for parsing the XML
     */
    public void setRootClass(String rootClass) {
        this.rootClass = rootClass;
    }
    
    /**
     * Sets the path that the root class should be bound to. 
     * This is optional and often unnecessary though can be used to ignore some wrapping
     * elements, such as the &lt;rss&gt; element in the RSS unit test.
     */
    public void setPath(String path) {
        this.path = path;
    }
    

    /**
     * Sets whether or not the current threads's context class loader
     * should be used to load the bean classes or not.
     * This can be useful if running inside a web application or inside some
     * application server.
     */
    public void setUseContextClassLoader(boolean useContextClassLoader) {
        this.useContextClassLoader = useContextClassLoader;
    }    

    /**
     * Sets the ClassLoader to be used to load bean classes from.
     * If this is not specified then either the ClassLoader used to load this tag library
     * is used or, if the 'useContextClassLoader' property is true, then the 
     * current threads context class loader is used instead.
     */    
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * @return the ClassLoader to be used to load bean classes.
     */
    protected ClassLoader getClassLoader() {
        if ( classLoader != null ) {
            return classLoader;
        }
        if ( useContextClassLoader ) {
            return Thread.currentThread().getContextClassLoader();
        }
        else {
            return getClass().getClassLoader();
        }
    }
}
