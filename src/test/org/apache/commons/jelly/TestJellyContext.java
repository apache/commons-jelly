package org.apache.commons.jelly;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 *
 * @author <a href="proyal@pace2020.com">peter royal</a>
 */
public class TestJellyContext extends TestCase
{
    public TestJellyContext( String s )
    {
        super( s );
    }

    public void testSetVariablesAndRetainContextEntry()
    {
        final JellyContext jc = new JellyContext();

        assertNotNull( "Initial variable of context", jc.getVariable( "context" ) );

        jc.setVariables( new HashMap() );

        assertNotNull( "Value after setVariables()", jc.getVariable( "context" ) );
    }
}
