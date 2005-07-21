/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.core;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import junit.framework.TestCase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This test illustrates pre-1.0 Jelly behavior that did not cache tags by default. Many user tag implementations
 * will assume that they are at an initalized state when doTag() is called, rather than still being "dirty" from a
 * prior run.
 *
 * @author <a href="mailto:proyal@apache.org">peter royal</a>
 */
public class TestUnexpectedTagCaching extends TestCase
{
    public void testExpectFreshTagOnEachRun() throws Exception
    {
        final JellyContext scriptContext = new JellyContext();

        scriptContext.registerTagLibrary( "a", new TestCachingTagLibrary() );

        final String scriptText = "<a:write xmlns:a=\"a\"><a:set>${message}</a:set></a:write>";
        final Script script = scriptContext.compileScript( new InputSource( new StringReader( scriptText ) ) );

        assertScriptResult( "one", script, scriptContext );
        assertScriptResult( "two", script, scriptContext );
        assertScriptResult( "three", script, scriptContext );
    }

    private static void assertScriptResult( final String message,
                                            final Script script,
                                            final JellyContext scriptContext ) throws JellyTagException
    {
        final JellyContext context = new JellyContext( scriptContext );

        context.setVariable( "message", message );

        final StringWriter writer = new StringWriter();

        script.run( context, XMLOutput.createXMLOutput( writer ) );

        assertEquals( "["+ message  + "]", writer.toString() );
    }

    public static class TestCachingTagLibrary extends TagLibrary
    {
        public TestCachingTagLibrary()
        {
            registerTag( "set", SetTag.class );
            registerTag( "write", WriteTag.class );
        }
    }

    public static class WriteTag extends TagSupport
    {
        private List m_strings = new ArrayList();

        public List getStrings()
        {
            return m_strings;
        }

        public void addString( final String string )
        {
            m_strings.add( string );
        }

        public void doTag( final XMLOutput output ) throws MissingAttributeException, JellyTagException
        {
            invokeBody( output );

            try
            {
                output.write( getStrings().toString() );
            }
            catch( SAXException e )
            {
                throw new JellyTagException( "Unable to write message", e );
            }
        }
    }

    public static class SetTag extends TagSupport
    {
        public void doTag( final XMLOutput output ) throws MissingAttributeException, JellyTagException
        {
            ( (WriteTag)getParent() ).addString( getBodyText() );
        }
    }
}