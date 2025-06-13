package org.apache.commons.jelly.tags.velocity;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * A tag that uses Velocity to render a specified template with the
 * JellyContext storing the results in either a variable in the
 * JellyContext or in a specified file.
 */
public class MergeTag extends VelocityTagSupport
{
    private static final String ENCODING = "ISO-8859-1";

    private String var;
    private String name;
    private String basedir;
    private String template;
    private String inputEncoding;
    private String outputEncoding;
    private boolean readOnly = true;

    @Override
    public void doTag( final XMLOutput output ) throws JellyTagException
    {
        if ( basedir == null || template == null )
        {
            throw new JellyTagException(
                    "This tag must define 'basedir' and 'template'" );
        }

        if ( name != null )
        {
            try {
                final Writer writer = new OutputStreamWriter(
                        new FileOutputStream( name ),
                        outputEncoding == null ? ENCODING : outputEncoding );
                mergeTemplate( writer );
                writer.close();
            }
            catch (final IOException e) {
                throw new JellyTagException(e);
            }
        }
        else if ( var != null )
        {
            final StringWriter writer = new StringWriter();
            mergeTemplate( writer );
            context.setVariable( var, writer.toString() );
        }
        else
        {
            throw new JellyTagException(
                    "This tag must define either 'name' or 'var'" );
        }
    }

    /**
     * Merges the Velocity template with the Jelly context.
     *
     * @param writer The output writer used to write the merged results.
     * @throws Exception If an exception occurs during the merge.
     */
    private void mergeTemplate( final Writer writer ) throws JellyTagException
    {
        final JellyContextAdapter adapter = new JellyContextAdapter( getContext() );
        adapter.setReadOnly( readOnly );

        try {
            getVelocityEngine( basedir ).mergeTemplate(
                template,
                inputEncoding == null ? ENCODING : inputEncoding,
                adapter,
                writer );
        }
        catch (final Exception e) {
            throw new JellyTagException(e);
        }
    }

    /**
     * Sets the base directory used for loading of templates by the
     * Velocity file resource loader.
     *
     * @param basedir The directory where templates can be located by
     * the Velocity file resource loader.
     */
    public void setBasedir( final String basedir )
    {
        this.basedir = basedir;
    }

    /**
     * Sets the input encoding used in the specified template which
     * defaults to ISO-8859-1.
     *
     * @param encoding  The encoding used in the template.
     */
    public void setInputEncoding( final String encoding )
    {
        this.inputEncoding = encoding;
    }

    /**
     * Sets the file name for the merged output.
     *
     * @param name The name of the output file that is used to store the
     * results of the merge.
     */
    public void setName( final String name )
    {
        this.name = name;
    }

    /**
     * Sets the output encoding mode which defaults to ISO-8859-1 used
     * when storing the results of a merge in a file.
     *
     * @param encoding  The file encoding to use when writing the
     * output.
     */
    public void setOutputEncoding( final String encoding )
    {
        this.outputEncoding = encoding;
    }

    /**
     * Sets the read-only flag for this adapter which prevents
     * modifications in the Velocity context from propagating to the
     * JellyContext.
     *
     * @param readOnly <code>true</code> prevents modifications from
     * propagating (the default), or <code>false</code> which permits
     * modifications.
     */
    public void setReadOnly( final boolean readOnly )
    {
        this.readOnly = readOnly;
    }

    /**
     * Sets the fil ename of the template used to merge with the
     * JellyContext.
     *
     * @param template The file name of the template to be merged.
     */
    public void setTemplate( final String template )
    {
        this.template = template;
    }

    // -- Implementation ----------------------------------------------------

    /**
     * Sets the var used to store the results of the merge.
     *
     * @param var The var to set in the JellyContext with the results of
     * the merge.
     */
    public void setVar( final String var )
    {
        this.var = var;
    }
}

