/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: ProducerTask.java,v 1.3 2002/05/07 15:59:43 jstrachan Exp $
 */

package org.apache.commons.jelly.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.jelly.tags.ant.AntTagLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/** 
 * <p><code>JellyTask</code> is an Ant task which will 
 * run a given Jelly script.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.3 $
 */

public class JellyTask extends Task {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(Jelly.class);

    /** The JellyContext to use */
    private JellyContext context;

    /** The URL of the script to execute */
    private URL url;

    /** The URL of the root context for other scripts */
    private URL rootContext;
    
    /** The XML output */
    private XMLOutput xmlOutput;

    /** The file where output is going */
    private File output;    

    // Task interface
    //-------------------------------------------------------------------------
    
    /**
     * Excutes the Jelly script
     */
    public void execute() throws BuildException {
        try {
            log( "Running script: " + getUrl() );
            if ( output != null ) {
                log( "Sending output to: " + output );
            }
            
            Script script = compileScript();
            JellyContext context = getJellyContext();
            context.setVariable( "project", getProject() );
            script.run( context, getXMLOutput() );
            getXMLOutput().flush();
        }
        catch (Exception e) {
            throw new BuildException(e, getLocation() );
        }
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
     * Sets the script file to use
     */
    public void setFile(File file) throws MalformedURLException {
        setUrl( file.toURL() );
    }
    
    /**
     * Sets the output to generate
     */
    public void setOutput(File output) throws IOException {
        this.output = output;
        xmlOutput = XMLOutput.createXMLOutput( new FileWriter( output ) );
    }
    
    public XMLOutput getXMLOutput() throws IOException {
        if (xmlOutput == null) {
            xmlOutput = XMLOutput.createXMLOutput( System.out );
        }
        return xmlOutput;
    }
    
    /**
     * Sets the XMLOutput used
     */
    public void setXMLOutput(XMLOutput xmlOutput) {
        this.xmlOutput = xmlOutput;
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
            JellyContext parentContext =  new JellyContext(getRootContext(), new URL(text));
            context = new AntJellyContext(getProject() , parentContext);
            
            // register the Ant tag library
            context.registerTagLibrary( "jelly:ant", new AntTagLibrary() );
        }
        return context;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                
    
    /**
     * Compiles the script
     */
    protected Script compileScript() throws Exception {
        XMLParser parser = new XMLParser();
        parser.setContext(getJellyContext());
        Script script = parser.parse(getUrl().toString());
        script = script.compile();
        if (log.isDebugEnabled()) {
            log.debug("Compiled script: " + getUrl());
        }
        return script;
    }
    
    
    /**
     * @return the URL for the relative file name or absolute URL 
     */
    protected URL resolveURL(String name) throws MalformedURLException {
		File file = getProject().resolveFile(name);
        if (file.exists()) {
            return file.toURL();
        }
        return new URL(name);
    }
}
