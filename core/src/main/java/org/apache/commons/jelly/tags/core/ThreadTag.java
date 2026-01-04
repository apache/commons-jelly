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
package org.apache.commons.jelly.tags.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** A tag that spawns the contained script in a separate thread
  */
public class ThreadTag extends TagSupport  {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ThreadTag.class);

    /** Thread Name */
    private String name = null;

    /** The destination of output */
    private XMLOutput xmlOutput;

    /** Should we close the underlying output */
    private boolean closeOutput;

    public ThreadTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        if ( xmlOutput == null ) {
            // lets default to system.out
            try {
                xmlOutput = XMLOutput.createXMLOutput( System.out );
            } catch (final UnsupportedEncodingException e) {
                throw new JellyTagException(e);
            }
        }

        // lets create a child context
        final JellyContext newContext = context.newJellyContext();

        final Thread thread = new Thread(
            () -> {
                try {
                    getBody().run(newContext, xmlOutput);
                    if (closeOutput) {
                        xmlOutput.close();
                    }
                    else {
                        xmlOutput.flush();
                    }
                }
                catch (final Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("error running thread tag", e);
                    }
                }
            }
        );
        if ( name != null ) {
            thread.setName( name );
        }
        thread.start();
    }

    /**
     * Sets the file which is generated from the output
     * @param name The output file name
     */
    public void setFile(final String name) throws IOException {
        this.closeOutput = true;
        setXmlOutput( XMLOutput.createXMLOutput(new FileOutputStream(name)) );
    }

    /**
     * Sets the name of the thread.
     * @param name The name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the destination of output
     */
    public void setXmlOutput(final XMLOutput xmlOutput) {
        this.closeOutput = false;
        this.xmlOutput = xmlOutput;
    }
}
