/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */

package org.apache.commons.jelly.tags.antlr;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyException;

import antlr.Tool;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.security.Permission;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class AntlrTag extends TagSupport
{
    private List grammars;
    private File outputDir;

    public AntlrTag()
    {
        this.grammars = new ArrayList( 1 );
    }


    // Tag interface
    //------------------------------------------------------------------------- 
    
    /** 
     * Evaluate the body to register all the various goals and pre/post conditions
     * then run all the current targets
     */
    public void doTag(final XMLOutput output) throws Exception
    {
        if ( this.outputDir == null )
        {
            throw new MissingAttributeException( "outputDir" );
        }

        invokeBody( output );

        Iterator grammarIter = this.grammars.iterator();
        String eachGrammar = null;

        String sourceDir = (String) getContext().getVariable( "maven.antlr.src.dir" );
        File grammar = null;

        while ( grammarIter.hasNext() )
        {
            eachGrammar = ((String) grammarIter.next()).trim();

            grammar = new File( sourceDir,
                                eachGrammar );

            File generated = getGeneratedFile( grammar.getPath() );

            if ( generated.exists() )
            {
                if ( generated.lastModified() > grammar.lastModified() )
                {
                    // it's more recent, skip.
                    return;
                }
            }

            if ( ! generated.getParentFile().exists() )
            {
                generated.getParentFile().mkdirs();
            }

            String[] args = new String[]
                {
                    "-o",
                    generated.getParentFile().getPath(),
                    grammar.getPath(),
                };
            
            SecurityManager oldSm = System.getSecurityManager();

            System.setSecurityManager( NoExitSecurityManager.INSTANCE );
            
            try
            {
                Tool.main( args );
            }
            catch (SecurityException e)
            {
                if ( ! e.getMessage().equals( "exitVM-0" ) )
                {
                    throw new JellyException( e );
                }
            }
            finally
            {
                System.setSecurityManager( oldSm );
            }
        }
    }

    protected File getGeneratedFile(String grammar) throws JellyException
    {
        File grammarFile = new File( grammar );

        String generatedFileName = null;

        String className = null;
        String packageName = "";

        try {

            BufferedReader in = new BufferedReader(new FileReader(grammar));
            
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                int extendsIndex = line.indexOf(" extends ");
                if (line.startsWith("class ") &&  extendsIndex > -1) {
                    generatedFileName = line.substring(6, extendsIndex).trim();
                    break;
                }
                else if ( line.startsWith( "package" ) ) {
                    packageName = line.substring( 8 ).trim();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new JellyException("Unable to determine generated class",
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

    void addGrammar(String grammar)
    {
        this.grammars.add( grammar );
    }

    public void setOutputDir(File outputDir)
    {
        this.outputDir = outputDir;
    }

    public File getOutputDir()
    {
        return this.outputDir;
    }
}

class NoExitSecurityManager extends SecurityManager
{
    static final NoExitSecurityManager INSTANCE = new NoExitSecurityManager();

    private NoExitSecurityManager()
    {
    }

    public void checkPermission(Permission permission)
    {
    }

    public void checkExit(int status)
    {
        throw new SecurityException( "exitVM-" + status );
    }
}
