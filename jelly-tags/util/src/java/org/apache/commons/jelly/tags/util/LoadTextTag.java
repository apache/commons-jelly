/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/util/src/java/org/apache/commons/jelly/tags/util/LoadTextTag.java,v 1.2 2003/01/25 19:31:48 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/25 19:31:48 $
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
 * $Id: LoadTextTag.java,v 1.2 2003/01/25 19:31:48 morgand Exp $
 */
package org.apache.commons.jelly.tags.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * A tag which loads text from a file or URI into a Jelly variable. 
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public class LoadTextTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(LoadTextTag.class);

    private String var;
    private File file;
    private String uri;

    public LoadTextTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (var == null) {
            throw new MissingAttributeException("var");
        }
        if (file == null && uri == null) {
            throw new JellyTagException( "This tag must have a 'file' or 'uri' specified" );
        }
        Reader reader = null;
        if (file != null) {
            if (! file.exists()) {
                throw new JellyTagException( "The file: " + file + " does not exist" );
            }
            
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new JellyTagException("could not find the file",e);
            }
        }   
        else {
            InputStream in = context.getResourceAsStream(uri);
            if (in == null) {
                throw new JellyTagException( "Could not find uri: " + uri );
            }
            // @todo should we allow an encoding to be specified?
            reader = new InputStreamReader(in);
        }
        
        String text = null;
        
        try {
            text = loadText(reader);
        } 
        catch (IOException e) {
            throw new JellyTagException(e);
        }
        
        context.setVariable(var, text);
    }

    // Properties
    //------------------------------------------------------------------------- 
    
    /**
     * Sets the name of the variable which will be exported with the text value of the
     * given file.
     */
    public void setVar(String var) {
        this.var = var;
    }
    /**
     * Returns the file.
     * @return File
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the uri.
     * @return String
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns the var.
     * @return String
     */
    public String getVar() {
        return var;
    }

    /**
     * Sets the file to be parsed as text
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Sets the uri to be parsed as text. 
     * This can be an absolute URL or a relative or absolute URI 
     * from this Jelly script or the root context.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }


    // Implementation methods
    //------------------------------------------------------------------------- 

    /**
     * Loads all the text from the given Reader
     */
    protected String loadText(Reader reader) throws IOException {
        StringBuffer buffer = new StringBuffer();
        
        // @todo its probably more efficient to use a fixed char[] buffer instead
		try {        
	        BufferedReader bufferedReader = new BufferedReader(reader);
	        while (true) {
	            String line = bufferedReader.readLine();
	            if (line == null) {
	                break;
	            }
	            else {
	                buffer.append(line);
	                buffer.append('\n');
	            }
	        }
	        return buffer.toString();
		}
		finally {
			try {
				reader.close();
			}
			catch (Exception e) {
				log.error( "Caught exception closing Reader: " + e, e);
			}
		}
    }
}
