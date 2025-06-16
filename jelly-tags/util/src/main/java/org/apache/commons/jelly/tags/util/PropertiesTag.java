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

package org.apache.commons.jelly.tags.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag which loads a properties file from a given file name or URI
 * which are loaded into the current context.
 */
public class PropertiesTag extends TagSupport {
    private String file;
    private String uri;
    private String var;

    public PropertiesTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        if (file == null && uri == null) {
            throw new JellyTagException("This tag must define a 'file' or 'uri' attribute");
        }
        InputStream is = null;
        if (file != null) {
            final File f = new File(file);
            if (!f.exists()) {
                throw new JellyTagException("file: " + file + " does not exist!");
            }

            try {
                is = new FileInputStream(f);
            } catch (final FileNotFoundException e) {
                throw new JellyTagException(e);
            }
        }
        else {
            is = context.getResourceAsStream(uri);
            if (is == null) {
                throw new JellyTagException( "Could not find: " + uri );
            }
        }
        final Properties props = new Properties();

        try {
            props.load(is);
        } catch (final IOException e) {
            throw new JellyTagException("properties tag could not load from file",e);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (final IOException ioe) {

                }
            }
        }

        if (var != null) {
            context.setVariable(var, props);
        }
        else {
            final Enumeration propsEnum = props.propertyNames();
            while (propsEnum.hasMoreElements()) {
                final String key = (String) propsEnum.nextElement();
                final String value = props.getProperty(key);

                // @todo we should parse the value in case its an Expression
                context.setVariable(key, value);
            }
        }

    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Sets the file name to be used to load the properties file.
     */
    public void setFile(final String file) {
        this.file = file;
    }

    /**
     * Sets the URI of the properties file to use. This can be a full URL or a relative URI
     * or an absolute URI to the root context of this JellyContext.
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * If this is defined then a Properties object containing all the
     * properties will be created and exported, otherwise the current variable
     * scope will be set to the value of the properties.
     *
     * @param var The var to set
     */
    public void setVar(final String var) {
        this.var = var;
    }

}
