/*
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
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
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

/**
 * 
 * 
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 */
/** <p><code>Embedded</code> provides easy means to embed JellyEngine <br/>
 *  and use Jelly scripts within an application</p>
 *  A typical usage:<br/>
 *  <code><br/>
 *     Embedded embedded = new Embedded();<br/>
 *     embedded.setOutputStream(new ByteArrayOutputStream());<br/>
 *     embedded.setVariable("some-var","some-object");<br/>
 *     .....<br/>
 *     embedded.setScript(scriptAsString);<br/>
 *     //or one can do.<br/>
 *     //embedded.setScript(scriptAsInputStream);<br/>
 *     <br/>
 *     boolean bStatus=embedded.execute();<br/>
 *     if(!bStatus) //if error<br/>
 *     {<br/>
 *         String errorMsg=embedded.getErrorMsg();<br/>
 *     }<br/>
 *  </code>  <br/>
 * 
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 */
public class Embedded {
    /** Jelly Engine */
    Jelly m_jellyEngine = new Jelly();
    /** JellyContext*/
    private JellyContext m_context = new JellyContext();
    /** Compiled Script Object*/
    private Script m_script;
    /** Input script as stream*/
    private InputStream m_inputStream;
    /** Output Stream */
    private OutputStream m_outputStream;
    /** Output(default System.out) */
    private XMLOutput m_output =
        XMLOutput.createXMLOutput(new OutputStreamWriter(System.out));
    /** Exception thrown during compilation of script*/
    Exception m_scriptCompilationException;
    /** boolean value indicating whether the script has been successfully compiled or NOT */
    boolean m_scriptCompiled = false;
    /** ErrorMsg*/
    private String m_errorMsg;
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(Embedded.class);

    /**
     * Default Constructor
     * 
     */
    public Embedded() {
        //m_context.setClassLoader(new TagLibraryClassLoader(m_context));
    }
    
    /**
     * Method setContext.
     * @param context
     */
    public void setContext(JellyContext context) {
        m_context = context;
    }

    /**
     * Method getContext.
     * @return JellyContext
     */
    public JellyContext getContext() {
        return m_context;
    }

    /**
     * Set a new variable within the context for the script to use.
     * @param name
     * @param value
     */
    public void setVariable(String name, Object value) {
        m_context.setVariable(name, value);
    }

    /**
     * Set the input script 
     * @param scriptAsString
     */
    public void setScript(String scriptAsString) {

        try {
            URL url = resolveURL(scriptAsString);
            m_inputStream = url.openStream();
        }
        catch (MalformedURLException e) {
            //Encapsulate the string within
            m_inputStream = new ByteArrayInputStream(scriptAsString.getBytes());
        }
        catch (IOException e) {
            //Error reading from the URL
            m_inputStream = null;
        }

        compileScriptAndKeep();

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
     * Set the input stream 
     * @param scriptAsInputStream
     */
    public void setScript(InputStream scriptAsInputStream) {
        m_inputStream = scriptAsInputStream;
        compileScriptAndKeep();
    }

    /**
     * Compile the script 
     */
    private void compileScriptAndKeep() {
        XMLParser parser = new XMLParser();
        parser.setContext(m_context);
        try {
            m_script = parser.parse(m_inputStream);
            m_script = m_script.compile();
            m_scriptCompiled = true;
        }
        catch (IOException e) {
            m_scriptCompilationException = e;
            m_scriptCompiled = false;
        }
        catch (SAXException e) {
            m_scriptCompilationException = e;
            m_scriptCompiled = false;
        }
        catch (Exception e) {
            m_scriptCompilationException = e;
            m_scriptCompiled = false;
        }
    }

    /**
     * Method setOutputStream.
     * @param outputStream
     */
    public void setOutputStream(OutputStream outputStream) {
        m_outputStream = outputStream;
        m_output =
            XMLOutput.createXMLOutput(new OutputStreamWriter(m_outputStream));
    }

    /** 
     * Registers the given tag library class name against the given namespace URI.
     * The class will be loaded via the given ClassLoader
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(String namespaceURI, String className) {
        if (m_context != null)
            m_context.registerTagLibrary(namespaceURI, className);
    }

    /** 
     * Registers the given tag library against the given namespace URI.
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(String namespaceURI, TagLibrary taglib) {
        if (m_context != null)
            m_context.registerTagLibrary(namespaceURI, taglib);
    }

    /**
     * Returns the errorMsg.
     * @return String
     */
    public String getErrorMsg() {
        return m_errorMsg;
    }

    /**
     * Sets the errorMsg.
     * @param errorMsg The errorMsg to set
     */
    private void setErrorMsg(String errorMsg) {
        m_errorMsg = errorMsg;
    }

    /**
     * Execute the jelly script and capture the errors (ifany)within.
     * 
     * @throws JellyException
     */
    public boolean execute() {
        if (log.isDebugEnabled())
            log.debug("Starting Execution");
        //If script has not been compiled then return the errorMsg that occured during compilation
        if (!m_scriptCompiled) {
            if (log.isErrorEnabled())
                log.error(m_scriptCompilationException.getMessage());
            setErrorMsg(m_scriptCompilationException.getMessage());
            return false;
        }
        if (m_inputStream == null) {
            if (log.isErrorEnabled())
                log.error("[Error] Input script-resource NOT accessible");
            setErrorMsg("[Error] Input script-resource NOT accessible");
            return false;
        }
        try {
            m_script.run(m_context, m_output);
            m_outputStream.close();
            m_output.flush();
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

}
