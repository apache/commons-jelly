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

package org.apache.commons.jelly.avalon;

import java.io.File;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
 */
public class JellyServiceImpl implements JellyService, Configurable {

    private final boolean m_configured = false;
    private final Map m_scripts = new HashMap();

    /**
     * Constructor for JellyService.
     */
    public JellyServiceImpl() {
    }

    /**
     * <p>Configures the Jelly Service with named scripts.</p>
     *
     * <p>
     * The configuration looks like:
     * </p>
     * <pre>
     * &lt;jelly&gt;
     * &nbsp;&nbsp;&lt;script&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;script name&lt;/name&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;url validate="false"&gt;url to script file&lt;/url&gt;
     * &nbsp;&nbsp;&lt;/script&gt;
     * &lt;/jelly&gt;
     * </pre>
     * <p>
     *   Where each &lt;script&gt; element defines a separate script. The validate attribute
     *   on the url tag is optional and defaults to false.
     * </p>
     *
     * @param config The configuration
     * @throws ConfigurationException
     */
    @Override
    public void configure( final Configuration config ) throws ConfigurationException {
        if ( m_configured ) {
            throw new ConfigurationException( "configure may only be executed once" );
        }

        if ( !"jelly".equals( config.getName() ) ) {
            throw new ConfigurationException( "Expected <jelly> but got " + config.getName() );
        }

        // Configure named scripts
        final Configuration[] scripts = config.getChildren( "scripts" );
        for (int i = 0; i < scripts.length; i++) {
            final String name = config.getChild( "name" ).getValue();

            // Try to load and compile the script
            try {
                final String scriptName = config.getChild( "url" ).getValue();
                // Try to load the script via file, then by URL, then by classloader
                URL url = null;
                final File file = new File( scriptName );
                if ( file.exists() ) {
                    url = file.toURL();
                }
                else {
                    try {
                        url = new URL( scriptName );
                    }
                    catch ( final MalformedURLException mfue ) {
                      // Last try, via classloader
                      url = getClass().getResource( scriptName );
                    }
                }

                // All attempts failed...
                if ( url == null ) {
                    throw new ConfigurationException( "Could not find script [" + scriptName + "]" );
                }

                // Get the script and store it
                final Jelly jelly = new Jelly();
                jelly.setUrl( url );
                final boolean validate = config.getChild( "url" ).getAttributeAsBoolean( "validate",  false );
                jelly.setValidateXML( validate );
                final Script script = jelly.compileScript();

                m_scripts.put( name, script );
            }
            catch ( final Throwable t ) {
                throw new ConfigurationException( "Could not load script [" + name + "]: " + t.getMessage() );
            }
        }
    }

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

     /**
     * @see org.apache.commons.jelly.avalon.JellyService#runNamedScript(String, Map)
     */
    @Override
    public Map runNamedScript( final String name, final Map params ) throws Exception {
        return runNamedScript(name, params, createXMLOutput());
    }

     /**
     * @see org.apache.commons.jelly.avalon.JellyService#runNamedScript(String, Map, OutputStream)
     */
    @Override
    public Map runNamedScript( final String name, final Map params, final OutputStream out ) throws Exception {
        final XMLOutput xmlOutput = XMLOutput.createXMLOutput( out );
        final Map answer = runNamedScript(name, params, xmlOutput);
        xmlOutput.flush();
        return answer;
    }

     /**
     * @see org.apache.commons.jelly.avalon.JellyService#runNamedScript(String, Map, XMLOutput)
     */
    @Override
    public Map runNamedScript( final String name, final Map params, final XMLOutput output ) throws Exception {
        if ( !m_scripts.containsKey( name ) ) {
            throw new JellyException( "No script exists for script name [" + name + "]" );
        }

        final Script script = (Script)m_scripts.get( name );
        final JellyContext context = createJellyContext();

        context.setVariables( params );

        script.run( context, output );
        return context.getVariables();
    }

    // Configurable interface
    //-------------------------------------------------------------------------

    /**
     * @see org.apache.commons.jelly.avalon.JellyService#runScript(String, Map)
     */
    @Override
    public Map runScript( final String url, final Map params ) throws Exception {
        return runScript(url, params, createXMLOutput());
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * @see org.apache.commons.jelly.avalon.JellyService#runScript(String, Map, OutputStream)
     */
    @Override
    public Map runScript( final String url, final Map params, final OutputStream out ) throws Exception {
        final XMLOutput xmlOutput = XMLOutput.createXMLOutput( out );
        final Map answer = runScript(url, params, xmlOutput);
        xmlOutput.flush();
        return answer;
    }

    /**
     * @see org.apache.commons.jelly.avalon.JellyService#runScript(String, Map, XMLOutput)
     */
    @Override
    public Map runScript( final String url, final Map params, final XMLOutput output ) throws Exception {
        URL actualUrl = null;
        try {
           actualUrl = new URL( url );
        }
        catch ( final MalformedURLException x ) {
            throw new JellyException( "Could not find script at URL [" + url + "]: " +
                                        x.getMessage(), x );
        }

        // Set up the context
        final JellyContext context = createJellyContext();
        context.setVariables( params );

        // Run the script
        context.runScript(url, output);
        return context.getVariables();
    }

}

