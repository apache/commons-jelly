/*
 * /home/cvs/jakarta-commons-sandbox/jelly/jelly-tags/avalon/src/java/org/apache/commons/jelly/avalon/JellyService.java,v 1.1 2003/01/22 09:19:30 jstrachan Exp
 * 1.1
 * 2003/01/22 09:19:30
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
 * JellyService.java,v 1.1 2003/01/22 09:19:30 jstrachan Exp
 */

package org.apache.commons.jelly.avalon;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

// Avalon
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

// Jelly
import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/**
 * An Avalon based service for executing Jelly scripts. The
 * service allows executing a script based on a name as well
 * as by a URL.
 * 
 * @author <a href="mailto:robert@bull-enterprises.com">Robert McIntosh</a>
 * @version 1.1
 */
public class JellyService implements Configurable {
    
    private boolean m_configured = false;
    private Map m_scripts = new HashMap();

	/**
	 * Constructor for JellyService.
	 */
	public JellyService() {
		super();
	}
    
    /**
     * Executes a named script with the supplied
     * Map of parameters.
     *
     * @param params Parameters to be supplied to the script
     * @return All of the variables from the JellyContext
     * @exception Exception if the script raises some kind of exception while processing
     */
    public Map runNamedScript( String name, Map params ) throws Exception {
        return runNamedScript(name, params, createXMLOutput());
    }

    /**
     * Executes a named script with the supplied
     * Map of parameters.
     *
     * @param name is the name of the script to run
     * @param params Parameters to be supplied to the script
     * @param output is the XMLOutput for any output to be sent
     * @return All of the variables from the JellyContext
     * @exception Exception if the script raises some kind of exception while processing
     */
    public Map runNamedScript( String name, Map params, XMLOutput output ) throws Exception {
        if( !m_scripts.containsKey( name ) )
            throw new JellyException( "No script exists for script name [" + name + "]" );

        Script script = (Script)m_scripts.get( name );
        JellyContext context = createJellyContext();

        context.setVariables( params );

        script.run( context, output );
        return context.getVariables();
    }

    /**
     * Executes a named script with the supplied
     * Map of parameters and send the output of the script
     * to the supplied output stream.
     *
     * @param name is the name of the script to run
     * @param params Parameters to be supplied to the script
     * @param out is the outputStream for output to be sent
     * @return All of the variables from the JellyContext
     * @exception Exception if the script raises some kind of exception while processing
     */
    public Map runNamedScript( String name, Map params, OutputStream out ) throws Exception {
        XMLOutput xmlOutput = XMLOutput.createXMLOutput( out );
        Map answer = runNamedScript(name, params, xmlOutput);
        xmlOutput.flush();
        return answer;
    }

    /**
     * Runs a script from the supplied url
     *
     * @param url The URL of the script
     * @param params Parameters to be supplied to the script
     * @param output is the XMLOutput where output of the script will go
     * @return All of the variables from the JellyContext
     */
    public Map runScript( String url, Map params, XMLOutput output ) throws Exception {
        URL actualUrl = null;
        try {
           actualUrl = new URL( url );
        }
        catch( MalformedURLException x ) {
            throw new JellyException( "Could not find script at URL [" + url + "]: " +
                                        x.getMessage(), x );
        }

        // Set up the context
        JellyContext context = createJellyContext();
        context.setVariables( params );

        // Run the script
        context.runScript(url, output);
        return context.getVariables();
    }

    /**
     * Runs a script from the supplied url and sends the output of the script to
     * the supplied OutputStream.
     * 
     * @param url The URL of the script
     * @param params Parameters to be supplied to the script
     * @param out The OutputStream to send the output of the script to
     * @return All of the variables from the JellyContext
     * @exception Exception if the script raises some kind of exception while processing
     */
    public Map runScript( String url, Map params, OutputStream out ) throws Exception {
        XMLOutput xmlOutput = XMLOutput.createXMLOutput( out );
        Map answer = runScript(url, params, xmlOutput);
        xmlOutput.flush();
        return answer;
    }

    /**
     * Runs a script from the supplied url
     *
     * @param url The URL of the script
     * @param params Parameters to be supplied to the script
     * @return All of the variables from the JellyContext
     * @exception Exception if the script raises some kind of exception while processing
     */
    public Map runScript( String url, Map params ) throws Exception {
        return runScript(url, params, createXMLOutput());
    }

    
    // Configurable interface
    //-------------------------------------------------------------------------
    
	    
    /**
     * <p>Configures the Jelly Service with named scripts.</p>
     * 
     * <p>
     * The configuration looks like:
     * </p>
     * <p>
     * &lt;jelly&gt;<br />
     * &nbsp;&nbsp;&lt;script&gt;<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;script name&lt;/name&gt;<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;url validate="false"&gt;url to script file&lt;/url&gt;<br />
     * &nbsp;&nbsp;&lt;/script&gt;<br />
     * &lt;/jelly&gt;<br />
     * </p>
     * <p>
     *   Where each &lt;script&gt; element defines a seperate script. The validate attribute
     *   on the url tag is optional and defaults to false.
     * </p>
     * 
     * @param config The configuration
     * @exception ConfigurationException
     */
    public void configure( Configuration config ) throws ConfigurationException {
        if( m_configured )
            throw new ConfigurationException( "configure may only be executed once" );
            
        if( !"jelly".equals( config.getName() ) )
            throw new ConfigurationException( "Expected <jelly> but got " + config.getName() );   
        
        // Configure named scripts
        Configuration[] scripts = config.getChildren( "scripts" );
        for (int i = 0; i < scripts.length; i++) {
			String name = config.getChild( "name" ).getValue();
            
            // Try to load and compile the script
            try {
                String scriptName = config.getChild( "url" ).getValue();
                // Try to load the script via file, then by URL, then by classloader
                URL url = null;
                File file = new File( scriptName );
                if( file.exists() ) {
                    url = file.toURL();
                }
                else {
                    try {
                        url = new URL( scriptName );                        
                    } 
                    catch( MalformedURLException mfue ) {
                      // Last try, via classloader
                      url = getClass().getResource( scriptName );                      
                    }  
                }
                
                // All atempts failed...
                if( url == null )
                    throw new ConfigurationException( "Could not find script [" + scriptName + "]" );
                
                // Get the script and store it
                Jelly jelly = new Jelly();
                jelly.setUrl( url );
                boolean validate = config.getChild( "url" ).getAttributeAsBoolean( "validate",  false );
                jelly.setValidateXML( validate );
                Script script = jelly.compileScript();
                
                m_scripts.put( name, script );
            }
            catch( Throwable t ) {
                throw new ConfigurationException( "Could not load script [" + name + "]: " + t.getMessage() );
            }
		}
    }


    // Implementation methods
    //-------------------------------------------------------------------------
    
    /**
     * Factory method to create a new JellyContext instance. Derived classes
     * could overload this method to provide a custom JellyContext instance.
     */
    protected JellyContext createJellyContext() {
        return new JellyContext();
    }

    /**
     * Factory method to create a new XMLOutput to give to scripts as they run.
     * Derived classes could overload this method, such as to pipe output to
     * some log file etc.
     */
    protected XMLOutput createXMLOutput() {
        // output will just be ignored
        return new XMLOutput();
    }

}

