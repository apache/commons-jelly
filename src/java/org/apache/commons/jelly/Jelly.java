/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/Jelly.java,v 1.13 2002/07/25 01:51:20 werken Exp $
 * $Revision: 1.13 $
 * $Date: 2002/07/25 01:51:20 $
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
 * $Id: Jelly.java,v 1.13 2002/07/25 01:51:20 werken Exp $
 */

package org.apache.commons.jelly;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * <p><code>Jelly</code> is a helper class which is capable of
 * running a Jelly script. This class can be used from the command line
 * or can be used as the basis of an Ant task.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.13 $
 */
public class Jelly {
    
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(Jelly.class);
    
    /** The JellyContext to use */
    private JellyContext context;
    
    /** The URL of the script to execute */
    private URL url;
    
    /** The URL of the root context for other scripts */
    private URL rootContext;
    
    
    public Jelly() {
    }
    
    public static void main(String[] args) throws Exception {

        try
        {
            if (args.length <= 0) {
                System.out.println("Usage: Jelly scriptFile [outputFile]");
                return;
            }
            
            Jelly jelly = new Jelly();
            jelly.setScript(args[0]);
            
            // later we might wanna add some command line arguments 
            // checking stuff using commons-cli to specify the output file
            // and input file via command line arguments
            Writer writer = ( args.length > 1 ) 
                ? new FileWriter( args[1] ) 
                : new OutputStreamWriter( System.out );
            
            Script script = jelly.compileScript();
            XMLOutput output = XMLOutput.createXMLOutput(writer);
            
            // add the system properties and the command line arguments
            JellyContext context = jelly.getJellyContext();
            context.setVariable("args", args);
            script.run(context, output);
            writer.close();
        }
        catch (JellyException e)
        {
            Throwable cause = e.getCause();

            if ( cause != null )
            {
                cause.printStackTrace();
            }
            else
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Compiles the script
     */
    public Script compileScript() throws Exception {
        XMLParser parser = new XMLParser();
        parser.setContext(getJellyContext());
        Script script = parser.parse(getUrl().openStream());
        script = script.compile();
        if (log.isDebugEnabled()) {
            log.debug("Compiled script: " + getUrl());
        }
        return script;
    }
    
    
    // Properties
    //-------------------------------------------------------------------------                
    
    /** 
     * Sets the script URL to use as an absolute URL or a relative filename
     */
    public void setScript(String script) throws MalformedURLException {
        setUrl(resolveURL(script));
    }
    
    public URL getUrl() {
        return url;
    }
    
    /** 
     * Sets the script URL to use 
     */
    public void setUrl(URL url) {
        this.url = url;
    }
    
    /** 
     * Gets the root context
     */
    public URL getRootContext() throws MalformedURLException {
        if (rootContext == null) {
            rootContext = new File(System.getProperty("user.dir")).toURL();
        }
        return rootContext;
    }
    
    /** 
     * Sets the root context
     */
    public void setRootContext(URL rootContext) {
        this.rootContext = rootContext;
    }
    
    /**
     * The context to use
     */
    public JellyContext getJellyContext() throws MalformedURLException {
        if (context == null) {
            // take off the name off the URL
            String text = getUrl().toString();
            int idx = text.lastIndexOf('/');
            text = text.substring(0, idx + 1);
            context = new JellyContext(getRootContext(), new URL(text));
        }
        return context;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                
    /**
     * @return the URL for the relative file name or absolute URL 
     */
    protected URL resolveURL(String name) throws MalformedURLException {
        File file = new File(name);
        if (file.exists()) {
            return file.toURL();
        }
        return new URL(name);
    }
}
