/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/util/CommandLineParser.java,v 1.4 2003/10/09 21:21:30 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/09 21:21:30 $
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
 * $Id: CommandLineParser.java,v 1.4 2003/10/09 21:21:30 rdonkin Exp $
 */

package org.apache.commons.jelly.util;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.ParseException;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/**
 * Utility class to parse command line options using CLI.
 * Using a separate class allows us to run Jelly without
 * CLI in the classpath when the command line interface
 * is not in use.
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author Morgan Delagrange
 * @version $Revision: 1.4 $
 */
public class CommandLineParser {
    
    protected static CommandLineParser _instance = new CommandLineParser();

    public static CommandLineParser getInstance() {
        return _instance;
    }

    /**
     * Parse out the command line options and configure
     * the give Jelly instance.
     * 
     * @param args   options from the command line
     * @exception JellyException
     *                   if the command line could not be parsed
     */
    public void invokeCommandLineJelly(String[] args) throws JellyException {
        CommandLine cmdLine = null;
        try {
            cmdLine = parseCommandLineOptions(args);
        } catch (ParseException e) {
            throw new JellyException(e);
        }

        // get the -script option. If there isn't one then use args[0]
        String scriptFile = null;
        if (cmdLine.hasOption("script")) {
            scriptFile = cmdLine.getOptionValue("script");
        } else {
            scriptFile = args[0];
        }

        //
        // Use classloader to find file
        //
        URL url = this.getClass().getClassLoader().getResource(scriptFile);
        // check if the script file exists
        if (url == null && !(new File(scriptFile)).exists()) {
            throw new JellyException("Script file " + scriptFile + " not found");
        }

        try {
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

        } catch (Exception e) {
            throw new JellyException(e);
        }

    }

    /**
     * Parse the command line using CLI. -o and -script are reserved for Jelly.
     * -Dsysprop=sysval is support on the command line as well.
     */
    public CommandLine parseCommandLineOptions(String[] args) throws ParseException {
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

}
