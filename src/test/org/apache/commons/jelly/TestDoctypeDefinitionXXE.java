package org.apache.commons.jelly;

import junit.framework.TestCase;

import java.net.URL;

/**
 * A test class to validate doctype definitions' declaration of external
 * calls using custom xml tags. Specifically we test some changes in {@link JellyContext}
 * along with {@link org.apache.commons.jelly.parser.XMLParser}.
 *
 * @author chotmpki
 */
public class TestDoctypeDefinitionXXE extends TestCase
{
    public TestDoctypeDefinitionXXE( String s )
    {
        super( s );
    }

    public void testDoctypeDefinitionXXEDefaultMode() throws JellyException
    {
        JellyContext context = new JellyContext();
        URL url = this.getClass().getResource("doctypeDefinitionXXE.jelly");
        try
        {
            context.runScript(url, null);
        } catch (JellyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof java.net.ConnectException) {
                fail("doctypeDefinitionXXE.jelly attempted to connect to http://127.0.0.1:4444");
            } else if (cause instanceof org.xml.sax.SAXParseException) {
                // Success.
            } else {
                fail("Unknown exception: " + e.getMessage());
            }
        }
    }

    public void testDoctypeDefinitionXXEAllowDTDCalls() throws JellyException
    {
        JellyContext context = new JellyContext();
        context.setAllowDtdToCallExternalEntities(true);
        URL url = this.getClass().getResource("doctypeDefinitionXXE.jelly");
        try
        {
            context.runScript(url, null);
        } catch (JellyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof java.net.ConnectException) {
                //success
            } else if (cause instanceof org.xml.sax.SAXParseException) {
                fail("doctypeDefinitionXXE.jelly did not attempt to connect to http://127.0.0.1:4444");
            } else {
                fail("Unknown exception: " + e.getMessage());
            }
        }
    }
}
