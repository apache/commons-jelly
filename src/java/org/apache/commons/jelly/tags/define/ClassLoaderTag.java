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

package org.apache.commons.jelly.tags.define;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 * Creates a new <code>URLClassLoader</code> to dynamically
 * load tags froms.
 * 
 * @author <a href="mailto:stephenh@chase3000.com">Stephen Haberman</a>
 * @version $Revision: 1.1 $
 */
public class ClassLoaderTag extends BeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ClassLoaderTag.class);
    
    /** The name to export the classloader to. */
    private String var;
    
    /** The URL to load the classes from. */
    private String url;
    
    // Properties
    //-------------------------------------------------------------------------                    
    
    /**
     * @return the variable to store the class loader in
     */
    public String getVar() {
        return this.var;
    }
    
    /** 
     * @param var the variable to store the class loader in
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * @return the url to load the classes from
     */
    public String getUrl() {
        return this.url;
    }
    
    /**
     * @param url the url to load the classes from
     */
    public void setUrl(String url) {
        this.url = url;
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    public void doTag(XMLOutput output) throws Exception {
        if ( getVar() == null ) {
            throw new MissingAttributeException( "var" );
        }
        if ( getUrl() == null ) {
            throw new MissingAttributeException( "url" );
        }
                
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = getClass().getClassLoader();
        }
        
        URLClassLoader newClassLoader = new URLClassLoader(
            new URL[] { new URL(getUrl()) },
            parent );
        
        log.debug("Storing the new classloader in " + getVar());
        
        context.setVariable(getVar(), newClassLoader);
    }
    
}
