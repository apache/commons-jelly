/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/Jelly.java,v 1.18 2002/10/09 21:03:08 morgand Exp $
 * $Revision: 1.18 $
 * $Date: 2002/10/09 21:03:08 $
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
 * $Id: Jelly.java,v 1.18 2002/10/09 21:03:08 morgand Exp $
 */

package org.apache.commons.jelly;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.ArrayList;

import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.cli.*;

/** 
 * <p><code>Jelly</code> is a helper class which is capable of
 * running a Jelly script. This class can be used from the command line
 * or can be used as the basis of an Ant task.</p> Command line usage is as follows:
 *
 * <pre>
 * jelly [scriptFile] [-script scriptFile -o outputFile -Dsysprop=syspropval]
 * </pre>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.18 $
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
    
    /** Whether we have loaded the properties yet */
    private boolean loadedProperties = false;

    private String defaultNamespaceURI = null;
        
    public Jelly() {
    }

    /**
     * Usage: jelly [scriptFile] [-script scriptFile -o outputFile -Dsysprop=syspropval]
     */
    public static void main(String[] args) throws Exception {

        try {
            if (args.length <= 0) {
                System.out.println("Usage: jelly [scriptFile] [-script scriptFile -o outputFile -Dsysprop=syspropval]");
                return;
            }

            // parse the command line options using CLI
            CommandLine cmdLine = parseCommandLineOptions(args);

            // get the -script option. If there isn't one then use args[0]
            String scriptFile = null;
            if (cmdLine.hasOption("script")) {
                scriptFile = cmdLine.getOptionValue("script");
            } else {
                scriptFile = args[0];
            }

            // check if the script file exists
            if (!(new File(scriptFile)).exists()) {
                System.out.println("Script file " + scriptFile + " not found");
                return;
            }

            // extract the -o option for the output file to use
            final XMLOutput output = cmdLine.hasOption("o") ?
                    XMLOutput.createXMLOutput(new FileWriter(cmdLine.getOptionValue("o"))) :
                    XMLOutput.createXMLOutput(System.out);

            Jelly jelly = new Jelly();
            jelly.setScript(scriptFile);

            Script script = jelly.compileScript();

            // add the system properties and the command line arguments
            JellyContext context = jelly.getJellyContext();
            context.setVariable("args", args);
            context.setVariable("commandLine", cmdLine);
            script.run(context, output);

            // now lets wait for all threads to close
            Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            output.close();
                        }
                        catch (Exception e) {
                            // ignore errors
                        }
                    }
                }
            );
        }
        catch (JellyException e) {
            Throwable cause = e.getCause();

            if (cause != null) {
                cause.printStackTrace();
            }
            else {
                e.printStackTrace();
            }
        }
    }

    /**
     * Parse the command line using CLI. -o and -script are reserved for Jelly.
     * -Dsysprop=sysval is support on the command line as well.
     */
    private static CommandLine parseCommandLineOptions(String[] args) throws ParseException {
        // create the expected options
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption("o", true, "Output file");
        cmdLineOptions.addOption("script", true, "Jelly script to run");

        // -D options will be added to the system properties
        Properties sysProps = System.getProperties();

        // filter the system property setting from the arg list
        // before passing it to the CLI parser
        ArrayList filteredArgList = new ArrayList();

        for (int i=0;i<args.length;i++) {
            String arg = args[i];

            // if this is a -D property parse it and add it to the system properties.
            // -D args will not be copied into the filteredArgList.
            if (arg.startsWith("-D") && (arg.length() > 2)) {
                arg = arg.substring(2);
                StringTokenizer toks = new StringTokenizer(arg, "=");

                if (toks.countTokens() == 2) {
                    // add the tokens to the system properties
                    sysProps.setProperty(toks.nextToken(), toks.nextToken());
                } else {
                    System.err.println("Invalid system property: " + arg);
                }

            } else {
                // add this to the filtered list of arguments
                filteredArgList.add(arg);

                // add additional "-?" options to the options object. if this is not done
                // the only options allowed would be "-o" and "-script".
                if (arg.startsWith("-") && arg.length() > 1) {
                    if (!(arg.equals("-o") && arg.equals("-script"))) {
                        cmdLineOptions.addOption(arg.substring(1, arg.length()), true, "dynamic option");
                    }
                }
            }
        }

        // make the filteredArgList into an array
        String[] filterArgs = new String[filteredArgList.size()];
        filteredArgList.toArray(filterArgs);

        // parse the command line
        Parser parser = new GnuParser();
        return parser.parse(cmdLineOptions, filterArgs);
    }

    /**
     * Compiles the script
     */
    public Script compileScript() throws Exception {
        if (! loadedProperties) {
            loadedProperties = true;
            loadJellyProperties();
        }
        
        XMLParser parser = new XMLParser();
        parser.setContext(getJellyContext());
        parser.setDefaultNamespaceURI(defaultNamespaceURI);
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


    /**
     * Set the jelly namespace to use for unprefixed elements.
     * Will be overridden by an explicit namespace in the
     * XML document.
     * 
     * @param namespace jelly namespace to use (e.g. 'jelly:core')
     */
    public void setDefaultNamespaceURI(String namespace) {
        defaultNamespaceURI = namespace;
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

    /**
     * Attempts to load jelly.properties from the current directory,
     * the users home directory or from the classpath
     */
    protected void loadJellyProperties() {
        InputStream is = null;
    
        String userDir = System.getProperty("user.home");
        File f = new File(userDir + File.separator + "jelly.properties");
        try {
            if (f.exists()) {
                is = new FileInputStream(f);
                loadProperties(is);
            }
        }
        catch (Exception e) {
            log.error( "Caught exception while loading: " + f.getName() + ". Reason: " + e, e );
        }
    
        f = new File("jelly.properties");
        try {
            if (f.exists()) {
                is = new FileInputStream(f);
                loadProperties(is);
            }
        }
        catch (Exception e) {
            log.error( "Caught exception while loading: " + f.getName() + ". Reason: " + e, e );
        }
        
        
        is = getClass().getClassLoader().getResourceAsStream("jelly.properties");
        if (is != null) {
            try {
                loadProperties(is);
            }
            catch (Exception e) {
                log.error( "Caught exception while loading jelly.properties from the classpath. Reason: " + e, e );
            }
        }
    }

    /**
     * Loads the properties from the given input stream 
     */    
    protected void loadProperties(InputStream is) throws Exception {
        JellyContext context = getJellyContext();
        Properties props = new Properties();
        props.load(is);
        Enumeration enum = props.propertyNames();
        while (enum.hasMoreElements()) {
            String key = (String) enum.nextElement();
            String value = props.getProperty(key);
            
            // @todo we should parse the value in case its an Expression
            context.setVariable(key, value);
        }
    }
}
