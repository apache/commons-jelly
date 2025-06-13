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

package org.apache.commons.jelly.tags.antlr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import antlr.Tool;

public class AntlrTag extends TagSupport
{
    private final List grammars;
    private File outputDir;

    public AntlrTag()
    {
        this.grammars = new ArrayList( 1 );
    }

    // Tag interface
    //-------------------------------------------------------------------------

    void addGrammar(final String grammar)
    {
        this.grammars.add( grammar );
    }

    /**
     * Evaluate the body to register all the various goals and pre/post conditions
     * then run all the current targets
     */
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException
    {
        if ( this.outputDir == null )
        {
            throw new MissingAttributeException( "outputDir" );
        }

        invokeBody( output );

        final Iterator grammarIter = this.grammars.iterator();
        String eachGrammar = null;

        final String sourceDir = (String) getContext().getVariable( "maven.antlr.src.dir" );
        File grammar = null;

        while ( grammarIter.hasNext() )
        {
            eachGrammar = ((String) grammarIter.next()).trim();

            grammar = new File( sourceDir,
                                eachGrammar );

            final File generated = getGeneratedFile( grammar.getPath() );

            if ( generated.exists() && (generated.lastModified() > grammar.lastModified()) )
            {
                // it's more recent, skip.
                return;
            }

            if ( ! generated.getParentFile().exists() )
            {
                generated.getParentFile().mkdirs();
            }

            final String[] args = {
                "-o",
                generated.getParentFile().getPath(),
                grammar.getPath(),
            };

            final SecurityManager oldSm = System.getSecurityManager();

            System.setSecurityManager( NoExitSecurityManager.INSTANCE );

            try
            {
                Tool.main( args );
            }
            catch (final SecurityException e)
            {
                if ( ! e.getMessage().equals( "exitVM-0" ) )
                {
                    throw new JellyTagException( e );
                }
            }
            finally
            {
                System.setSecurityManager( oldSm );
            }
        }
    }

    protected File getGeneratedFile(final String grammar) throws JellyTagException
    {
        final File grammarFile = new File( grammar );

        String generatedFileName = null;

        final String className = null;
        String packageName = "";

        try {

            final BufferedReader in = new BufferedReader(new FileReader(grammar));

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                final int extendsIndex = line.indexOf(" extends ");
                if (line.startsWith("class ") &&  extendsIndex > -1) {
                    generatedFileName = line.substring(6, extendsIndex).trim();
                    break;
                }
                if ( line.startsWith( "package" ) ) {
                    packageName = line.substring( 8 ).trim();
                }
            }
            in.close();
        } catch (final Exception e) {
            throw new JellyTagException("Unable to determine generated class",
                                     e);
        }
        if (generatedFileName == null) {
            return null;
        }

        File genFile = null;

        if ( "".equals( packageName ) )
        {
            genFile = new File( getOutputDir(),
                                generatedFileName + ".java" );
        }
        else
        {
            String packagePath = packageName.replace( '.',
                                                      File.separatorChar );

            packagePath = packagePath.replace( ';',
                                               File.separatorChar );

            genFile = new File( new File( getOutputDir(), packagePath),
                                generatedFileName + ".java" );
        }

        return genFile;
    }

    public File getOutputDir()
    {
        return this.outputDir;
    }

    public void setOutputDir(final File outputDir)
    {
        this.outputDir = outputDir;
    }
}

final class NoExitSecurityManager extends SecurityManager
{
    static final NoExitSecurityManager INSTANCE = new NoExitSecurityManager();

    private NoExitSecurityManager()
    {
    }

    @Override
    public void checkExit(final int status)
    {
        throw new SecurityException( "exitVM-" + status );
    }

    @Override
    public void checkPermission(final Permission permission)
    {
    }
}
