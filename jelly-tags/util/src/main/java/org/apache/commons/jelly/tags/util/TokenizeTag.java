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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

public class TokenizeTag extends TagSupport
{
    private String var;
    private String delim;

    public TokenizeTag()
    {
    }

    // Tag interface
    //-------------------------------------------------------------------------

    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if ( this.var == null )
        {
            throw new MissingAttributeException( "var" );
        }

        if ( this.delim == null )
        {
            throw new MissingAttributeException( "delim" );
        }

        final StringTokenizer tokenizer = new StringTokenizer( getBodyText(),
                                                         this.delim );

        final List tokens = new ArrayList();

        while ( tokenizer.hasMoreTokens() )
        {
            tokens.add( tokenizer.nextToken() );
        }

        getContext().setVariable( this.var,
                                  tokens );
    }

    /** The delimiter that separates the tokens */
    public void setDelim(final String delim)
    {
        this.delim = delim;
    }

    /** The variable name to hold the list of tokens */
    public void setVar(final String var)
    {
        this.var = var;
    }

}
