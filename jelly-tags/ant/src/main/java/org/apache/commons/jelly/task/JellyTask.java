/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.jelly.tags.ant.AntTagLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.xml.sax.SAXException;

/**
 * <p><code>JellyTask</code> is an Ant task which will
 * run a given Jelly script.
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
     * Compiles the script
     */
    protected Script compileScript() throws JellyException {
        final XMLParser parser = new XMLParser();

        Script script = null;
        try {
            parser.setContext(getJellyContext());
            script = parser.parse(getUrl().toString());
        }
        catch (final IOException | SAXException e) {
            throw new JellyException(e);
        }
        script = script.compile();

        if (log.isDebugEnabled()) {
            log.debug("Compiled script: " + getUrl());
        }
        return script;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Executes the Jelly script
     */
    @Override
    public void execute() throws BuildException {
        try {
            log( "Running script: " + getUrl() );
            if ( output != null ) {
                log( "Sending output to: " + output );
            }

            final Script script = compileScript();
            final JellyContext context = getJellyContext();
            context.setVariable( "project", getProject() );
            script.run( context, getXMLOutput() );
            getXMLOutput().flush();
        }
        catch (final Exception e) {
            throw new BuildException(e, getLocation() );
        }
    }

    /**
     * The context to use
     */
    public JellyContext getJellyContext() throws MalformedURLException {
        if (context == null) {
            // take off the name off the URL
            String text = getUrl().toString();
            final int idx = text.lastIndexOf('/');
            text = text.substring(0, idx + 1);
            final JellyContext parentContext =  new JellyContext(getRootContext(), new URL(text));
            context = new AntJellyContext(getProject() , parentContext);

            // register the Ant tag library
            context.registerTagLibrary( "jelly:ant", new AntTagLibrary() );
        }
        return context;
    }

    /**
     * Gets the root context
     */
    public URL getRootContext() throws MalformedURLException {
        if (rootContext == null) {
            rootContext = new File(System.getProperty("user.dir")).toURI().toURL();
        }
        return rootContext;
    }

    public URL getUrl() {
        return url;
    }

    public XMLOutput getXMLOutput() throws IOException {
        if (xmlOutput == null) {
            xmlOutput = XMLOutput.createXMLOutput( System.out );
        }
        return xmlOutput;
    }

    /**
     * @return the URL for the relative file name or absolute URL
     */
    protected URL resolveURL(final String name) throws MalformedURLException {
        final File file = getProject().resolveFile(name);
        if (file.exists()) {
            return file.toURI().toURL();
        }
        return new URL(name);
    }

    /**
     * Sets the script file to use
     */
    public void setFile(final File file) throws MalformedURLException {
        setUrl( file.toURI().toURL() );
    }

    /**
     * Sets the output to generate
     */
    public void setOutput(final File output) throws IOException {
        this.output = output;
        xmlOutput = XMLOutput.createXMLOutput( new FileWriter( output ) );
    }

    /**
     * Sets the root context
     */
    public void setRootContext(final URL rootContext) {
        this.rootContext = rootContext;
    }

    /**
     * Sets the script URL to use as an absolute URL or a relative file name
     */
    public void setScript(final String script) throws MalformedURLException {
        setUrl(resolveURL(script));
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the script URL to use
     */
    public void setUrl(final URL url) {
        this.url = url;
    }

    /**
     * Sets the XMLOutput used
     */
    public void setXMLOutput(final XMLOutput xmlOutput) {
        this.xmlOutput = xmlOutput;
    }
}
