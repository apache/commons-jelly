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

import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.jelly.XMLOutput;

/**
 * An Avalon based service for executing Jelly scripts. The
 * service allows executing a script based on a name as well
 * as by a URL.
 * @version 1.1
 */
public interface JellyService {

    /**
     * Executes a named script with the supplied
     * Map of parameters.
     *
     * @param params Parameters to be supplied to the script
     * @return All of the variables from the JellyContext
     * @throws Exception if the script raises some kind of exception while processing
     */
    Map runNamedScript( String name, Map params ) throws Exception;

    /**
     * Executes a named script with the supplied
     * Map of parameters and send the output of the script
     * to the supplied output stream.
     *
     * @param name is the name of the script to run
     * @param params Parameters to be supplied to the script
     * @param out is the outputStream for output to be sent
     * @return All of the variables from the JellyContext
     * @throws Exception if the script raises some kind of exception while processing
     */
    Map runNamedScript( String name, Map params, OutputStream out ) throws Exception;

    /**
     * Executes a named script with the supplied
     * Map of parameters.
     *
     * @param name is the name of the script to run
     * @param params Parameters to be supplied to the script
     * @param output is the XMLOutput for any output to be sent
     * @return All of the variables from the JellyContext
     * @throws Exception if the script raises some kind of exception while processing
     */
    Map runNamedScript( String name, Map params, XMLOutput output ) throws Exception;

    /**
     * Runs a script from the supplied url
     *
     * @param url The URL of the script
     * @param params Parameters to be supplied to the script
     * @return All of the variables from the JellyContext
     * @throws Exception if the script raises some kind of exception while processing
     */
    Map runScript( String url, Map params ) throws Exception;

    /**
     * Runs a script from the supplied url and sends the output of the script to
     * the supplied OutputStream.
     *
     * @param url The URL of the script
     * @param params Parameters to be supplied to the script
     * @param out The OutputStream to send the output of the script to
     * @return All of the variables from the JellyContext
     * @throws Exception if the script raises some kind of exception while processing
     */
    Map runScript( String url, Map params, OutputStream out ) throws Exception;

    /**
     * Runs a script from the supplied url
     *
     * @param url The URL of the script
     * @param params Parameters to be supplied to the script
     * @param output is the XMLOutput where output of the script will go
     * @return All of the variables from the JellyContext
     */
    Map runScript( String url, Map params, XMLOutput output ) throws Exception;

}

