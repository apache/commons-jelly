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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag which evaluates its body if the given file is available.
 * The file can be specified via a File object or via a relative or absolute
 * URI from the current Jelly script.
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.3 $
 */
public class AvailableTag extends TagSupport {
	
	private File file;
	private String uri;

    public AvailableTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(final XMLOutput output) throws JellyTagException {
    	boolean available = false;
    	
    	if (file != null) {
    		available = file.exists();
    	}
    	else if (uri != null) {
            try {
                URL url = context.getResource(uri);
    		    String fileName = url.getFile();
                InputStream is = url.openStream();
                available = (is != null);
                is.close();
            } catch (MalformedURLException e) {
                throw new JellyTagException(e);
            } catch (IOException ioe) {
                available = false;
            } 
    	}
    	
    	if (available) {
    		invokeBody(output);
    	}
    }

    // Properties
    //------------------------------------------------------------------------- 


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
	 * Sets the file to use to test whether it exists or not.
	 * @param file the file to test for
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Sets the URI to use to test for availability. 
	 * The URI can be a full file based URL or a relative URI 
	 * or an absolute URI from the root context.
	 * 
	 * @param uri the URI of the file to test
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

}
