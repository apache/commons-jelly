/*
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */

package org.apache.commons.jelly.tags.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag which loads a properties file from a given file name or URI
 * which are loaded into the current context.
 * 
 * @author Jim Birchfield
 * @version $Revision: 1.2 $
 */
public class PropertiesTag extends TagSupport {
    private String file;
    private String uri;
    private String var;

    public PropertiesTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(final XMLOutput output) throws JellyTagException {
        if (file == null && uri == null) {
            throw new JellyTagException("This tag must define a 'file' or 'uri' attribute");
        }
        InputStream is = null;
        if (file != null) {
            File f = new File(file);
            if (!f.exists()) {
                throw new JellyTagException("file: " + file + " does not exist!");
            }
            
            try {
                is = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                throw new JellyTagException(e);
            }
        }
        else {
            is = context.getResourceAsStream(uri);
            if (is == null) {
                throw new JellyTagException( "Could not find: " + uri );
            }
        }
        Properties props = new Properties();
        
        try {
            props.load(is);
        } catch (IOException e) {
            throw new JellyTagException("properties tag could not load from file",e);
        }
        
        if (var != null) {
            context.setVariable(var, props);
        }
        else {
            Enumeration enum = props.propertyNames();
            while (enum.hasMoreElements()) {
                String key = (String) enum.nextElement();
                String value = props.getProperty(key);
                
                // @todo we should parse the value in case its an Expression
                context.setVariable(key, value);
            }
        }

    }

    // Properties
    //------------------------------------------------------------------------- 
    
    /**
     * Sets the file name to be used to load the properties file.
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Sets the URI of the properties file to use. This can be a full URL or a relative URI
     * or an absolute URI to the root context of this JellyContext.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * If this is defined then a Properties object containing all the
     * properties will be created and exported, otherwise the current variable
     * scope will be set to the value of the properties.
     * 
     * @param var The var to set
     */
    public void setVar(String var) {
        this.var = var;
    }

}
