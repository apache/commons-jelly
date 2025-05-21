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
package org.apache.commons.jelly.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/*
 *
 * <p><code>Embedded</code> provides easy means to embed JellyEngine
 * and use Jelly scripts within an application</p>
 * A typical usage:
 *  <pre>
 *     Embedded embedded = new Embedded();
 *     embedded.setOutputStream(new ByteArrayOutputStream());
 *     embedded.setVariable("some-var","some-object");
 *     ...
 *     embedded.setScript(scriptAsString);
 *     // or one can do
 *     // embedded.setScript(scriptAsInputStream)
 *
 *     boolean bStatus=embedded.execute();
 *     if (!bStatus) { // if error 
 *         String errorMsg=embedded.getErrorMsg();
 *     }
 *  </pre>
 */
public class Embedded {
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(Embedded.class);
    /** Jelly Engine */
    Jelly jellyEngine = new Jelly();
    /** JellyContext*/
    private JellyContext context = new JellyContext();
    /** Compiled Script Object*/
    private Script script;
    /** Input script as stream*/
    private InputStream inputStream;
    /** Output Stream */
    private OutputStream outputStream;
    /** Output(default System.out) */
    private XMLOutput output =
        XMLOutput.createXMLOutput(new OutputStreamWriter(System.out));
    /** Exception thrown during compilation of script*/
    Exception scriptCompilationException;
    /** Boolean value indicating whether the script has been successfully compiled or NOT */
    boolean scriptCompiled = false;
    /** ErrorMsg*/
    private String errorMsg;

    /**
     * Default Constructor
     *
     */
    public Embedded() {
        //m_context.setClassLoader(new TagLibraryClassLoader(m_context));
    }

    /**
     * Compile the script
     */
    private void compileScriptAndKeep() {
        XMLParser parser = new XMLParser();
        parser.setContext(context);
        scriptCompiled = false;
        try {
            script = parser.parse(inputStream);
            script = script.compile();
            scriptCompiled = true;
        }
        catch (IOException e) {
            scriptCompilationException = e;
        }
        catch (SAXException e) {
            scriptCompilationException = e;
        }
        catch (Exception e) {
            scriptCompilationException = e;
        }
    }

    /**
     * Execute the jelly script and capture the errors (ifany) within.
     */
    public boolean execute() {
        if (log.isDebugEnabled())
            log.debug("Starting Execution");
        //If script has not been compiled then return the errorMsg that occurredd during compilation
        if (!scriptCompiled) {
            if (log.isErrorEnabled())
                log.error(scriptCompilationException.getMessage());
            setErrorMsg(scriptCompilationException.getMessage());
            return false;
        }
        if (inputStream == null) {
            if (log.isErrorEnabled())
                log.error("[Error] Input script-resource NOT accessible");
            setErrorMsg("[Error] Input script-resource NOT accessible");
            return false;
        }
        try {
            script.run(context, output);
            outputStream.close();
            output.flush();
        }
        catch (Exception e) {
            if (log.isErrorEnabled())
                log.error(e.getMessage());
            setErrorMsg(e.getMessage());
            return false;
        }
        if (log.isDebugEnabled())
            log.debug("Done Executing");
        return true;
    }

    /**
     * Method getContext.
     * @return JellyContext
     */
    public JellyContext getContext() {
        return context;
    }

    /**
     * Returns the errorMsg.
     * @return String
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * Registers the given tag library class name against the given namespace URI.
     * The class will be loaded via the given ClassLoader
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(String namespaceURI, String className) {
        if (context != null)
            context.registerTagLibrary(namespaceURI, className);
    }

    /**
     * Registers the given tag library against the given namespace URI.
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(String namespaceURI, TagLibrary taglib) {
        if (context != null)
            context.registerTagLibrary(namespaceURI, taglib);
    }

    /**
     * @return the URL for the relative file name or absolute URL
     */
    private URL resolveURL(String name) throws MalformedURLException {
        File file = new File(name);
        if (file.exists()) {
            return file.toURL();
        }
        return new URL(name);
    }

    /**
     * Method setContext.
     * @param context
     */
    public void setContext(JellyContext context) {
        this.context = context;
    }

    /**
     * Sets the errorMsg.
     * @param errorMsg The errorMsg to set
     */
    private void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * Method setOutputStream.
     * @param outputStream
     */
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.output = XMLOutput.createXMLOutput(new OutputStreamWriter(this.outputStream));
    }

    /**
     * Sets the input stream
     * @param scriptAsInputStream
     */
    public void setScript(InputStream scriptAsInputStream) {
        inputStream = scriptAsInputStream;
        compileScriptAndKeep();
    }

    /**
     * Sets the input script
     * @param scriptAsString
     */
    public void setScript(String scriptAsString) {

        try {
            URL url = resolveURL(scriptAsString);
            inputStream = url.openStream();
        }
        catch (MalformedURLException e) {
            //Encapsulate the string within
            inputStream = new ByteArrayInputStream(scriptAsString.getBytes());
        }
        catch (IOException e) {
            //Error reading from the URL
            inputStream = null;
        }

        compileScriptAndKeep();

    }

    /**
     * Sets a new variable within the context for the script to use.
     * @param name
     * @param value
     */
    public void setVariable(String name, Object value) {
        context.setVariable(name, value);
    }

}
