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
package org.apache.commons.jelly.tags.validate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.SAXException;

/**
 * This tag creates a new Verifier of a schema as a variable
 * so that it can be used by a &lt;validate&gt; tag.
 */
public class VerifierTag extends TagSupport {

    /** The variable name to export the Verifier as */
    private String var;

    /** The URI to load the schema from */
    private String uri;

    /** The file to load the schema from */
    private File file;

    /** The system ID to use when parsing the schema */
    private String systemId;

    /** The factory used to create new schema verifier objects */
    private VerifierFactory factory;

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if ( var == null ) {
            throw new MissingAttributeException("var");
        }

        InputStream in = null;
        if ( uri != null ) {
            in = context.getResourceAsStream( uri );
            if ( in == null ) {
                throw new JellyTagException( "Could not find resource for uri: " + uri );
            }
        } else if (file != null) {
            try {
                in = new FileInputStream(file);
            } catch (final FileNotFoundException e) {
                throw new JellyTagException(e);
            }
        } else {
            final String text = getBodyText();
            in = new ByteArrayInputStream( text.getBytes() );
        }

        Verifier verifier = null;
        try {
            Schema schema = null;
            if (systemId != null) {
                schema = getFactory().compileSchema(in, systemId);
            }
            else if ( uri != null ) {
                schema = getFactory().compileSchema(in, uri);
            }
            else{
                schema = getFactory().compileSchema(in);
            }

            if ( schema == null ) {
                throw new JellyTagException( "Could not create a valid schema" );
            }

            verifier = schema.newVerifier();
        }
        catch (final VerifierConfigurationException | SAXException | IOException e) {
            throw new JellyTagException(e);
        }

        context.setVariable(var, verifier);
    }

    // Properties
    //-------------------------------------------------------------------------

    public VerifierFactory getFactory() throws JellyTagException {
        if ( factory == null ) {
            try {
                final ClassLoader loader = ClassLoaderUtils.getClassLoader(null, true, getClass());
                factory = (VerifierFactory)loader.loadClass(
                    "com.sun.msv.verifier.jarv.TheFactoryImpl").getConstructor().newInstance();
            } catch (final ReflectiveOperationException e) {
                throw new JellyTagException(e);
            }
        }
        return factory;
    }

    /**
     * Sets the factory used to create new schema verifier objects.
     * If none is provided then the default MSV factory is used.
     * <p>
     * jelly:optional
     * </p>
     */
    public void setFactory(final VerifierFactory factory) {
        this.factory = factory;
    }

    /**
     * Sets the {@link File} of the schema to parse. If no URI and no file is
     * specified then the body of this tag is used as the source of the schema
     * <p>
     * jelly:optional
     * </p>
     */
    public void setFile(final File aFile) {
        file = aFile;
    }

    /**
     * Sets the system ID used when parsing the schema
     * <p>
     * jelly:optional
     * </p>
     */
    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }

    /**
     * Sets the URI of the schema file to parse. If no URI and no file is
     * specified then the body of this tag is used as the source of the schema
     * <p>
     * jelly:optional
     * </p>
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * Sets the name of the variable that will be set to the new Verifier
     * <p>
     * jelly:required
     * </p>
     */
    public void setVar(final String var) {
        this.var = var;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

}
