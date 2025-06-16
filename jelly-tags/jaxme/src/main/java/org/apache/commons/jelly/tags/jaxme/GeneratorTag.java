/**
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
package org.apache.commons.jelly.tags.jaxme;

import java.io.File;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.impl.GeneratorImpl;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;

/**
 * Generates java objects using JaxMe.
 * This object can be marshalled into XML and the results unmarshalled
 * using JaxMe.
 */
public class GeneratorTag extends TagSupport {

    private String schemaUrl;
    private String target;

    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {

        if (schemaUrl == null) {
            throw new MissingAttributeException( "schemaUrl" );
        }

        if (target == null) {
            throw new MissingAttributeException( "target" );
        }



        final Generator generator = new GeneratorImpl();

        final JAXBSchemaReader reader = new JAXBSchemaReader();
        generator.setSchemaReader(reader);
        reader.setGenerator(generator);
        generator.setTargetDirectory(getTargetDirectory());

        System.out.println("Target: " + getTargetDirectory());

        try
        {
            final SchemaSG schemaSG = generator.generate(getSchemaFile());
        }
        catch (final Exception e)
        {
            throw new JellyTagException(e);
        }
    }

    private File getSchemaFile() throws JellyTagException {
        return new File(schemaUrl);
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public String getTarget() {
        return target;
    }


    private File getTargetDirectory() throws JellyTagException {
        return new File(target);
    }

    /**
     * Defines the schema against which the java object representations
     * should be generated.
     */
    public void setSchemaUrl(final String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    /**
     * Defines the target directory into which
     * the generated objects will be placed.
     */
    public void setTarget(final String target) {
        this.target = target;
    }
}
